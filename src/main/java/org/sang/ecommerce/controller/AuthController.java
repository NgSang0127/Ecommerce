package org.sang.ecommerce.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.request.LoginRequest;
import org.sang.ecommerce.request.RegistrationRequest;
import org.sang.ecommerce.response.LoginResponse;
import org.sang.ecommerce.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

	private final AuthenticationService service;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) throws MessagingException {
		service.register(request);
		return ResponseEntity.accepted().build();
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login( @Valid @RequestBody LoginRequest request){
		return ResponseEntity.ok(service.login(request));

	}

	@GetMapping("/activate-account")
	public void confirm(@RequestParam String token) throws MessagingException {
			service.activateAccount(token);
	}

}
