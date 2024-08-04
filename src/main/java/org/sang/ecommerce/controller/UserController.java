package org.sang.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.UserException;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	private final  UserService userService;

	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfileHandler(Authentication connectedUser) throws UserException {

		User user=((User)connectedUser.getPrincipal());
		return ResponseEntity.ok(user);
	}

}
