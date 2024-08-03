package org.sang.ecommerce.service;

import org.sang.ecommerce.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

	public User findUserById(Long userId);

	public User findUserProfileByJwt(String jwt);

}
