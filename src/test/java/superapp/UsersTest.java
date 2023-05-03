package superapp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import superapp.entities.NewUserBoundary;
import superapp.entities.UserBoundary;
import superapp.entities.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UsersTest {
	private RestTemplate restTemplate;
	private String baseUrl;
	private String baseAdminUrl;
	private String baseLoginUrl;
	private int port;
	private String springApplicationName;

	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApllicationName) {
		this.springApplicationName = springApllicationName;
	}

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void setup() {
		this.restTemplate = new RestTemplate();
		this.baseUrl = "http://localhost:" + this.port + "/superapp/users";
		this.baseLoginUrl = "http://localhost:" + this.port + "/superapp/users/login/{superapp}/{email}";
		this.baseAdminUrl = "http://localhost:" + this.port + "/superapp/admin/users";
	}

	/**
	 * Clean DB.
	 */
	@AfterEach
	public void tearDown () {
		this.restTemplate
			.delete(baseAdminUrl);
	}

	
	@Test
	public void testTheDatabaseIsCleanOnStartup() throws Exception {
		/*
		 * GIVEN the server is up
		 * WHEN I GET /superapp/admin/users
		 * THEN the server responds with status 2xx AND the server returns empty array
		 */
		UserBoundary[] allUsersInDatabase = getAllUsersFromDB();

		assertThat(allUsersInDatabase)
		.isNotNull()
		.isEmpty();

	}

	/**
	 * Check 'Create a new user'
	 */
	@Test
	public void testSuccessfullPost() {
		/*
		 * GIVEN the server is up AND the database is empty 
		 * WHEN I POST with http://localhost:8084/supperapp/users 
		 * {
		 * 		"email": "Liran@gmail.com",
		 * 		"username":"Liran"
		 * 		"role":"MINIAPP_USER",
		 * 		"avatar":"K",
		 * }
		 * THEN the database contains a single user with the email "Liran@gmail.com"
		 */
		NewUserBoundary newUserBoundary = createNewUserBoundary();

		UserBoundary postUser = postNewUserToDB(newUserBoundary);
		UserId postUserId = postUser.getUserId();
		
//		String url = this.baseUrl + "/login/" + springApplicationName + "/" + postUser.getUserId().getEmail();
		UserBoundary getUser = getSavedUserFromDB(postUserId.getSuperApp(), postUserId.getEmail());

		String extarctingValueEmail = "userId.email",
				extarctingValueAvatar = "avatar",
				extarctingValueUsername = "username";
		String checkingValueEmail = postUserId.getEmail(), 
				checkingValueAvatar = postUser.getAvatar(), 
				checkingValueUsername = postUser.getUsername();
		
		assertGetUserToPostUser(getUser, extarctingValueEmail, checkingValueEmail);
		assertGetUserToPostUser(getUser, extarctingValueAvatar, checkingValueAvatar);
		assertGetUserToPostUser(getUser, extarctingValueUsername, checkingValueUsername);

	}

	/**
	 * Check 'Login valid user and retrieve user details'
	 */
	@Test
	public void testSuccessfullPostUsingSpecificUserGet() {
		/*
		* GIVEN the server is up AND the database is empty 
		* WHEN I POST http://localhost:8084/superapp/users 
		* {
		* 		"avatar":"J",
		* 		"email":"Liran@gmail.com",
		* 		"username":"Liran",
		*		"role":"MINIAPP_USER"
		*	} 
		* THEN the database contains a single user with the email "Liran@gmail.com"
		*/
		NewUserBoundary newUserBoundary = createNewUserBoundary();
		UserBoundary postUser = postNewUserToDB(newUserBoundary);
		UserId postUserId = postUser.getUserId();

		UserBoundary getUser = getSavedUserFromDB(postUserId.getSuperApp(), postUserId.getEmail());

		String extarctingValueEmail = "userId.email";
		String checkingValueEmail = postUser.getUserId().getEmail();

		assertGetUserToPostUser(getUser, extarctingValueEmail, checkingValueEmail);

	}

	/**
	 * Check 'Update user details'
	 */
	@Test
	public void testSuccessfulPutUser() {
		
		/*
		* GIVEN the server is up AND the database contains at least one user
		* WHEN I PUT http://localhost:8084/superapp/users/{superapp}/{userEmail} with
		* {
		* 		"avatar":"abab",
		* 		"username":"abc",
		*		"role":"SUPERAPP_USER"
		*	} 
		* THEN the user in the DB will be updated with the new details
		*/
		NewUserBoundary newUserBoundary = createNewUserBoundary();
		UserBoundary postUser = postNewUserToDB(newUserBoundary);
		UserId postUserId = postUser.getUserId();

		UserBoundary getUser = getSavedUserFromDB(postUserId.getSuperApp(), postUserId.getEmail());
		UserId getUserId = getUser.getUserId();

		String newUsername = "abc", newAvatar = "abab", newRole = "SUPERAPP_USER";
		getUser.setUsername(newUsername);
		getUser.setAvatar(newAvatar);
		getUser.setRole(newRole);
//		UserBoundary getUser1 = this.restTemplate
//				.getForObject(baseLoginUrl, UserBoundary.class, postUser.getUserId());
		
		this.restTemplate
		.put(baseUrl + "/{superapp}/{userEmail}", getUser, getUserId.getSuperApp(), getUserId.getEmail());
		
		String extarctingValueUsername = "username";
		String extarctingValueAvatar = "avatar";
		String extarctingValueRole = "role";
		String checkingValueUsername = getUser.getUsername();
		String checkingValueAvatar = getUser.getAvatar();
		String checkingValueRole = getUser.getRole();
		
		UserBoundary getUpdatedUser = getSavedUserFromDB(getUserId.getSuperApp(), getUserId.getEmail());
		
		assertGetUserToPostUser(getUpdatedUser, extarctingValueUsername, checkingValueUsername);
		assertGetUserToPostUser(getUpdatedUser, extarctingValueAvatar, checkingValueAvatar);
		assertGetUserToPostUser(getUpdatedUser, extarctingValueRole, checkingValueRole);
	}
	
	/**
	 * Check 'Export all users'
	 */
	@Test
	public void testSuccessGetAll() {
		/*
		 * GIVEN the server is up AND the database is empty\not empty 
		 * WHEN I GET /superapp/admin/users
		 * THEN I get all objects
		 *
		 */
		UserBoundary[] arr = getAllUsersFromDB();
		assertThat(arr)
		.isNotNull();

	}

	/**
	 * Check 'Delete all users in superapp'
	 */
	@Test
	public void testSuccessfulDeleteAll() {
		/*
		 * GIVEN the server is up AND the database is empty\not empty 
		 * WHEN I DELETE /superapp/admin/users
		 *
		 * THEN I delete all users
		 */
		
		NewUserBoundary newUserBoundary1 = createNewUserBoundary();
		NewUserBoundary newUserBoundary2 = createNewUserBoundary("Liran1@gmail.com", "Liran1", "MINIAPP_USER", "J");
		int count = 2;
		
		UserBoundary postUser1 = postNewUserToDB(newUserBoundary1);
		UserBoundary postUser2 = postNewUserToDB(newUserBoundary2);
		
		String extarctingValueEmail = "userId.email";
		String checkingValueEmail1 = postUser1.getUserId().getEmail();
		String checkingValueEmail2 = postUser2.getUserId().getEmail();

		assertGetUserToPostUser(postUser1, extarctingValueEmail, checkingValueEmail1);
		assertGetUserToPostUser(postUser2, extarctingValueEmail, checkingValueEmail2);
		
		UserBoundary[] arrPre = getAllUsersFromDB();
		assertThat(arrPre)
		.isNotNull()
		.hasSize(count);
		
		this.restTemplate.delete(this.baseAdminUrl);

		UserBoundary[] arrPost = getAllUsersFromDB();
		assertThat(arrPost)
		.isNotNull()
		.hasSize(0);
		
	}
	
	/**
	 * Create a default NewUserBoundary object.
	 * @return
	 */
	private NewUserBoundary createNewUserBoundary() {
		NewUserBoundary newUserBoundary = new NewUserBoundary();
		newUserBoundary.setEmail("Liran@gmail.com");
		newUserBoundary.setUsername("Liran");
		newUserBoundary.setRole("MINIAPP_USER");
		newUserBoundary.setAvatar("K");

		return newUserBoundary;
	}
	
	/**
	 * Create a custom NewUserBoundary object.
	 * @param email
	 * @param username
	 * @param role
	 * @param avatar
	 * @return
	 */
	private NewUserBoundary createNewUserBoundary(String email, String username, String role, String avatar) {
		NewUserBoundary newUserBoundary = new NewUserBoundary();
		newUserBoundary.setEmail(email);
		newUserBoundary.setUsername(username);
		newUserBoundary.setRole(role);
		newUserBoundary.setAvatar(avatar);

		return newUserBoundary;
	}
	
	/**
	 * Asserts UserBoundary loaded has same value as UserBoundary created.
	 * @param savedUser
	 * @param extarctingValue value from object to extract
	 * @param checkingValue value from object to compare to
	 */
	private void assertGetUserToPostUser(UserBoundary savedUser, String extarctingValue, String checkingValue) {
		assertThat(savedUser)
			.isNotNull()
			.extracting(extarctingValue)
			.isEqualTo(checkingValue);
	}

	/**
	 * Post New User to DB
	 * @param newUserBoundary the new user
	 * @return the UserBoundary created as a result of CRUD Post method
	 */
	private UserBoundary postNewUserToDB(NewUserBoundary newUserBoundary) {
		return this.restTemplate
				.postForObject(this.baseUrl, newUserBoundary, UserBoundary.class);
	}

	/**
	 * Get User from DB
	 * @param superapp the app name
	 * @param email the user's email
	 * @return the user from the DB
	 */
	private UserBoundary getSavedUserFromDB(String superapp, String email) {
		return this.restTemplate
				.getForObject(baseLoginUrl, UserBoundary.class, superapp, email);
	}
	
	/**
	 * Get all users from DB.
	 * @return
	 */
	private UserBoundary[] getAllUsersFromDB() {
		return this.restTemplate.getForObject(this.baseAdminUrl, UserBoundary[].class);
	}
	
	
}