package controllersSrc;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import controllersAPI.UsersRelatedAPI;
import entities.UserBoundary;
import entities.UserID;
import entities.eUserRole;

@RestController
public class UsersRelatedController implements UsersRelatedAPI {
	
	@RequestMapping(
			path = {"/superapp/users/login/{superapp}/{email}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@Override
		public UserBoundary loginValidUserAndRetrieveUserDetails (
		@PathVariable("superapp") String superapp, 
		@PathVariable("email") String email){
												
		UserBoundary userBoundary = new UserBoundary(new UserID(superapp.toString(), email.toString()), 
				eUserRole.STUDENT, "gal.tesler", "someURL");

		return userBoundary;
	}

}
