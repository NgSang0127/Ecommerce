package org.sang.ecommerce.service;

import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.email.EmailService;
import org.sang.ecommerce.email.EmailTemplateName;
import org.sang.ecommerce.model.Token;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.repository.TokenRepository;
import org.sang.ecommerce.repository.UserRepository;
import org.sang.ecommerce.request.LoginRequest;
import org.sang.ecommerce.request.RegistrationRequest;
import org.sang.ecommerce.response.LoginResponse;
import org.sang.ecommerce.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
				.createdDate(LocalDateTime.now())
				.accountLocked(false)
				.enabled(false)
				.build();
		userRepo.save(savedUser);
		sendValidationEmail(savedUser);
	}

	private void sendValidationEmail(User user) throws MessagingException {
		var newToken = generateAndSaveActivationToken(user);
		// send email
		emailService.sendEmail(
				user.getEmail(),
				user.getFullName(),
				EmailTemplateName.ACTIVATE_ACCOUNT,
				activationUrl,
				newToken,
				"Account Activation"
		);
	}

	private String generateAndSaveActivationToken(User user) {
		// generateToken
		String generateToken = generateActivationCode(6);
		var token = Token.builder()
				.token(generateToken)
				.createdAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(15))
				.user(user)
				.build();
		tokenRepo.save(token);
		return generateToken;
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

	public LoginResponse login(LoginRequest request) {
		var auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()
				)
		);
		var claims = new HashMap<String, Object>();
		var user = ((User) auth.getPrincipal());
		claims.put("fullName", user.getFullName());
		var jwtToken = jwtService.generateToken(claims, user);
		return LoginResponse.builder()
				.token(jwtToken).build();
	}

	public void activateAccount(String token) throws MessagingException {
		Token savedToken = tokenRepo.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
		if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
			sendValidationEmail(savedToken.getUser());
			throw new RuntimeException("Activate token has expired. A new token has been sent to the same email");
		}
		var user = userRepo.findById(savedToken.getUser().getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		user.setEnabled(true);
		userRepo.save(user);
		savedToken.setValidatedAt(LocalDateTime.now());
		tokenRepo.save(savedToken);
	}
}
