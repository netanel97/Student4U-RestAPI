package superapp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserId;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UsersTest {
	private RestTemplate restTemplate;
	private String baseUrl;
	private String baseAdminUrl;
	private String baseLoginUrl;
	private UserBoundary superappUser;
	private int port;

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
	@BeforeEach
	@AfterEach
	public void tearDown () {
		this.superappUser = postNewUserToDB(createNewUserBoundary());
		this.restTemplate
				.delete(baseAdminUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}",superappUser.getUserId().getSuperapp(), superappUser.getUserId().getEmail());
	}

//	@AfterEach
//	public void tearDown (
//			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
//			@RequestParam(name = "userEmail", required = true) String userEmail) {
//		this.restTemplate
//			.delete(baseAdminUrl + "?userSuperapp={superapp}&userEmail={email}");
//	}

	/**
	 * Check 'Create a new user'
	 */
	@Test
	public void testSuccessfullPost() {
		/*
		 * GIVEN the server is up AND the database is empty 
		 * WHEN I POST with http://localhost:8084/superapp/users 
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
		UserBoundary getUser = getSavedUserFromDB(postUserId.getSuperapp(), postUserId.getEmail());

		String extractingValueEmail = "userId.email",
				extractingValueAvatar = "avatar",
				extractingValueUsername = "username";

		String checkingValueEmail = postUserId.getEmail(),
				checkingValueAvatar = postUser.getAvatar(),
				checkingValueUsername = postUser.getUsername();

		assertGetUserToPostUser(getUser, extractingValueEmail, checkingValueEmail);
		assertGetUserToPostUser(getUser, extractingValueAvatar, checkingValueAvatar);
		assertGetUserToPostUser(getUser, extractingValueUsername, checkingValueUsername);
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

		UserBoundary getUser = getSavedUserFromDB(postUserId.getSuperapp(), postUserId.getEmail());

		String extractingValueEmail = "userId.email";
		String checkingValueEmail = postUser.getUserId().getEmail();

		assertGetUserToPostUser(getUser, extractingValueEmail, checkingValueEmail);
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

		UserBoundary getUser = getSavedUserFromDB(postUserId.getSuperapp(), postUserId.getEmail());
		UserId getUserId = getUser.getUserId();

		String newUsername = "abc", newAvatar = "abab", newRole = "SUPERAPP_USER";
		getUser.setUsername(newUsername);
		getUser.setAvatar(newAvatar);
		getUser.setRole(newRole);

		this.restTemplate
				.put(baseUrl + "/{superapp}/{userEmail}", getUser, getUserId.getSuperapp(), getUserId.getEmail());

		String extractingValueUsername = "username";
		String extractingValueAvatar = "avatar";
		String extractingValueRole = "role";

		String checkingValueUsername = getUser.getUsername();
		String checkingValueAvatar = getUser.getAvatar();
		String checkingValueRole = getUser.getRole();

		UserBoundary getUpdatedUser = getSavedUserFromDB(getUserId.getSuperapp(), getUserId.getEmail());

		assertGetUserToPostUser(getUpdatedUser, extractingValueUsername, checkingValueUsername);
		assertGetUserToPostUser(getUpdatedUser, extractingValueAvatar, checkingValueAvatar);
		assertGetUserToPostUser(getUpdatedUser, extractingValueRole, checkingValueRole);
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
		this.superappUser = postNewUserToDB(createNewUserBoundary());
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

		this.superappUser = postUser1;

		String extarctingValueEmail = "userId.email";
		String checkingValueEmail1 = postUser1.getUserId().getEmail();
		String checkingValueEmail2 = postUser2.getUserId().getEmail();

		assertGetUserToPostUser(postUser1, extarctingValueEmail, checkingValueEmail1);
		assertGetUserToPostUser(postUser2, extarctingValueEmail, checkingValueEmail2);

		UserBoundary[] arrPre = getAllUsersFromDB();
		assertThat(arrPre)
				.isNotNull()
				.hasSize(count);

		this.restTemplate.delete(this.baseAdminUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}", this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		this.superappUser = postNewUserToDB(createNewUserBoundary());

		UserBoundary[] arrPost = getAllUsersFromDB();
		assertThat(arrPost)
				.isNotNull()
				.hasSize(1);

	}

	/**
	 * Create a default NewUserBoundary object.
	 * @return
	 */
	private NewUserBoundary createNewUserBoundary() {
		NewUserBoundary newUserBoundary = new NewUserBoundary();
		newUserBoundary.setEmail("Liran@gmail.com");
		newUserBoundary.setUsername("Liran");
		newUserBoundary.setRole("ADMIN");
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
	private void assertGetUserToPostUser(UserBoundary savedUser, String extractingValue, String checkingValue) {
		assertThat(savedUser)
				.isNotNull()
				.extracting(extractingValue)
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
		return this.restTemplate.getForObject(this.baseAdminUrl + "?userSuperapp={superapp}&userEmail={email}"
				, UserBoundary[].class, superappUser.getUserId().getSuperapp(), superappUser.getUserId().getEmail());
	}


}