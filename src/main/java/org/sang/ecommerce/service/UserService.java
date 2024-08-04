package org.sang.ecommerce.service;

import org.sang.ecommerce.exception.UserException;
import org.sang.ecommerce.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

	User findUserById(Long userId) throws UserException;

	User findUserProfileByJwt(String jwt) throws UserException;

}
