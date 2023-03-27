package controllersSrc;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import controllersAPI.UsersRelatedAPI;
import entities.NewUserBoundary;
import entities.UserBoundary;
import entities.UserID;
import entities.eUserRole;

@RestController
public class UsersRelatedController implements UsersRelatedAPI {

	@RequestMapping(path = { "/superapp/users/login/{superapp}/{email}" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Override
	public UserBoundary loginValidUserAndRetrieveUserDetails(@PathVariable("superapp") String superapp,
			@PathVariable("email") String email) {

		UserBoundary userBoundary = new UserBoundary(new UserID(email.toString()),
				eUserRole.STUDENT, "gal.tesler", "someURL");

		return userBoundary;
	}

	@RequestMapping(path = { "/superapp/users" }, method = { RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, // returns a new JSON
			consumes = { MediaType.APPLICATION_JSON_VALUE }) // takes a JSON as argument
	@Override
	public UserBoundary createANewUser(@RequestBody NewUserBoundary newUser) {
		UserBoundary created = new UserBoundary();
		created.setUserName(newUser.getNewUserName());
		created.setRole(newUser.getRole());
		created.setAvatarUrl(newUser.getAvatarUrl());
		created.setUserId(new UserID(newUser.getEmail()));
		System.err.println("CREATED A NEW USER!\n" + created.toString());

		return created;

	}

	@RequestMapping(path = { "/superapp/users/{superapp}/{userEmail}" }
			, method = { RequestMethod.PUT }, 
			consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	@Override
	public void updateUserDetails(@PathVariable("superapp") String superapp, @PathVariable("userEmail") String email,
			@RequestBody UserBoundary updatedUser) {

		/*
		 TODO
		 might first want to check if the user exists with given UserID(superapp,
		 email)
		 then override the old user with toUpdate

		 MAYBE CONSIDER USING exportAllUsers OR SOMETHING
		 
		 */	
		

		System.err.println("UPDATING USER OF USERID: " + new UserID(email) + " USING " + updatedUser);

	}

}
