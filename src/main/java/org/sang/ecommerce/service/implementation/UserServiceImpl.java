package org.sang.ecommerce.service.implementation;

import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.UserException;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.repository.UserRepository;
import org.sang.ecommerce.security.JwtService;
import org.sang.ecommerce.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {

	private final UserRepository userRepository;
	private final JwtService jwtService;

	@Override
	public User findUserById(Long userId) throws UserException {

		return userRepository.findById(userId).orElseThrow(()-> new UserException("User not found with id"+userId));
	}

	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
		String email=jwtService.extractUsername(jwt);
		return userRepository.findByEmail(email).orElseThrow(()->new UserException("User not found with jwt:"+jwt));
	}
}
