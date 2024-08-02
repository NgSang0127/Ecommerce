package org.sang.ecommerce.implementation;

import java.util.List;
import jdk.jshell.spi.ExecutionControl.UserException;
import org.sang.ecommerce.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

	public User findUserById(Long userId);

	public User findUserProfileByJwt(String jwt);

}
