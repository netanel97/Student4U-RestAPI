package superapp.controllersSrc;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import superapp.entities.NewUserBoundary;
import superapp.entities.UserBoundary;
import superapp.entities.UserId;
import superapp.logic.UsersService;

@RestController
public class UsersRelatedController {
	private UsersService usersService;

	@Autowired
	public void setUsersService(UsersService usersService) {
		this.usersService = usersService;
	}
	/**
	 * Login to specific user from DB. Receives HTTP Method 'GET'.
	 * 
	 * @param @PathVariable("superapp") String superapp
	 * @param @PathVariable ("email") String email
	 * @return UserBoundary specific user.
	 */
	@RequestMapping(path = { "/superapp/users/login/{superapp}/{email}" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public UserBoundary loginValidUserAndRetrieveUserDetails(@PathVariable("superapp") String superapp,
			@PathVariable("email") String email) {
		return usersService.login(superapp, email).orElseThrow(()->new RuntimeException("could not find user with id: " + superapp + "_" + email));
	}
	
	
	/**
	 * create a new user. Receives HTTP Method 'POST'.
	 * 
	 * @param @RequestBody NewUserBoundary newUser
	 * @return UserBoundary new user.
	 */
	@RequestMapping(path = { "/superapp/users" }, method = { RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, // returns a new JSON
			consumes = { MediaType.APPLICATION_JSON_VALUE }) // takes a JSON as argument

	public UserBoundary createANewUser(@RequestBody NewUserBoundary newUser) {
		UserBoundary userBoundary = new UserBoundary();
		userBoundary.setAvatar(newUser.getAvatar());
		userBoundary.setRole(newUser.getRole());
		userBoundary.setUserId(new UserId(newUser.getEmail()));
		userBoundary.setUsername(newUser.getUsername());
		return this.usersService.createUser(userBoundary);
	}

	/**
	 * update an existing user from DB. Receives HTTP Method 'PUT'.
	 * 
	 * @param @PathVariable("superapp") String superapp
	 * @param @PathVariable ("email") String email
	 * @param @RequestBody UserBoundary user Boundary
	 */
	
	@RequestMapping(path = { "/superapp/users/{superapp}/{userEmail}" }, method = { RequestMethod.PUT }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })

	public void updateUserDetails(@PathVariable("superapp") String superapp, @PathVariable("userEmail") String email,
			@RequestBody UserBoundary updatedUser) {
		this.usersService.updateUser(superapp, email, updatedUser);
	}

}
