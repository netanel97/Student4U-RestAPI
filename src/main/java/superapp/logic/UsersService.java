package superapp.logic;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import entities.NewUserBoundary;
import entities.UserBoundary;

public interface UsersService {

	UserBoundary loginValidUserAndRetrieveUserDetails(@PathVariable("superapp") String superapp, 
			@PathVariable("email") String email);
	
	UserBoundary createANewUser(@RequestBody NewUserBoundary newUser);
	
	void updateUserDetails(@PathVariable("superapp") String superapp, 
			@PathVariable("userEmail") String email,
			@RequestBody UserBoundary toUpdate);
}
