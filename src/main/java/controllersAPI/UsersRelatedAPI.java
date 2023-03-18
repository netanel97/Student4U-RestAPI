package controllersAPI;

import org.springframework.web.bind.annotation.PathVariable;

import entities.UserBoundary;

public interface UsersRelatedAPI {
	UserBoundary loginValidUserAndRetrieveUserDetails(@PathVariable("superapp") String superapp, 
			@PathVariable("email") String email);
}
