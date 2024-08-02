package org.sang.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.email.EmailService;
import org.sang.ecommerce.email.EmailTemplateName;
import org.sang.ecommerce.exception.OperationNotPermittedException;
import org.sang.ecommerce.model.EmailVerificationCode;
import org.sang.ecommerce.model.Token;
import org.sang.ecommerce.model.TokenType;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.repository.EmailVerificationCodeRepository;
import org.sang.ecommerce.repository.TokenRepository;
import org.sang.ecommerce.repository.UserRepository;
import org.sang.ecommerce.request.LoginRequest;
import org.sang.ecommerce.request.RegistrationRequest;
import org.sang.ecommerce.response.AuthenticationResponse;
import org.sang.ecommerce.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	@Value("${application.mailing.frontend.activation-url}")
	private String activationUrl;

	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final TokenRepository tokenRepo;
	private final EmailVerificationCodeRepository emailVerificationCodeRepo;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public void register(RegistrationRequest request) throws MessagingException {
		if (userRepo.findByEmail(request.getEmail()).isPresent()) {
			throw new IllegalStateException("Email is already used");
		}

		User savedUser = User.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.accountLocked(false)
				.enabled(false)
				.build();
		userRepo.save(savedUser);
		sendValidationEmail(savedUser);
	}

	private void sendValidationEmail(User user) throws MessagingException {
		var newCode = generateAndSaveActivationCode(user);
		// send email
		emailService.sendEmail(
				user.getEmail(),
				user.getFullName(),
				EmailTemplateName.ACTIVATE_ACCOUNT,
				activationUrl,
				newCode,
				"Account Activation"
		);
	}

	private String generateAndSaveActivationCode(User user) {
		// generateToken
		String generateCodeEmail = generateActivationCode(6);
		var code = EmailVerificationCode.builder()
				.code(generateCodeEmail)
				.createdAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(15))
				.user(user)
				.build();
		emailVerificationCodeRepo.save(code);
		return generateCodeEmail;
	}

	private String generateActivationCode(int length) {
		String characters = "0123456789";
		StringBuilder codeBuilder = new StringBuilder();
		SecureRandom secureRandom = new SecureRandom();
		for (int i = 0; i < length; i++) {
			int randomIndex = secureRandom.nextInt(characters.length());
			codeBuilder.append(characters.charAt(randomIndex));
		}
		return codeBuilder.toString();
	}

	public AuthenticationResponse login(LoginRequest request) {
		var auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()
				)
		);
		var claims = new HashMap<String, Object>();
		var user = ((User) auth.getPrincipal());
		claims.put("fullName", user.getFullName());//muốn cấu hình thêm gì vaào jwt thì thêm
		var jwtToken = jwtService.generateToken(claims, user);
		saveUserToken(user, jwtToken);
		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.build();
	}

	public void activateAccount(String token) throws MessagingException {
		EmailVerificationCode savedEmailVerificationCode =
				emailVerificationCodeRepo.findByCode(token).orElseThrow(() -> new RuntimeException("Invalid token"));
		if (LocalDateTime.now().isAfter(savedEmailVerificationCode.getExpiresAt())) {
			sendValidationEmail(savedEmailVerificationCode.getUser());
			throw new RuntimeException("Activate token has expired. A new token has been sent to the same email");
		}
		var user = userRepo.findById(savedEmailVerificationCode.getUser().getId())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		user.setEnabled(true);
		userRepo.save(user);
		savedEmailVerificationCode.setValidatedAt(LocalDateTime.now());
		emailVerificationCodeRepo.save(savedEmailVerificationCode);
	}

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder()
				.user(user)
				.token(jwtToken)
				.tokenType(TokenType.BEARER)
				.expired(false)
				.revoked(false)
				.build();
		tokenRepo.save(token);
	}

	public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new OperationNotPermittedException("User not authenticated");
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {//ko can kiem tra xac thuc nua
			var user = userRepo.findByEmail(userEmail).orElseThrow(
					() -> new UsernameNotFoundException("User not found")
			);
			if (jwtService.isTokenValid(refreshToken, user)) {
				// Xóa các token cũ
				var accessToken = jwtService.generateRefreshToken(user);
				tokenRepo.findByUser(user).forEach(token -> {
					token.setExpired(true);
					token.setRevoked(true);
					token.setToken(accessToken);
					token.setTokenType(TokenType.BEARER);
					tokenRepo.save(token);
				});

				return AuthenticationResponse.builder()
						.accessToken(accessToken)
						.build();
			}

		}
		return null;
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepo.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepo.saveAll(validUserTokens);
	}
}
