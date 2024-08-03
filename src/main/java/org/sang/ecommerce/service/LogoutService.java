package org.sang.ecommerce.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.repository.TokenRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

	private final TokenRepository tokenRepo;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt;
		if(authHeader == null || !authHeader.startsWith("Bearer ")){
			return ;
		}
		jwt=authHeader.substring(7);
		var savedToken = tokenRepo.findByToken(jwt)
				.orElse(null);

		if(savedToken !=null){
			savedToken.setExpired(true);
			savedToken.setRevoked(true);
			tokenRepo.save(savedToken);
			SecurityContextHolder.clearContext();
		}
	}
}
