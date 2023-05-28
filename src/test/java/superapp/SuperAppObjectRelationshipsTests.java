package superapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.object.*;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SuperAppObjectRelationshipsTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private final String CHILDREN = "/children";
	private final String PARENTS = "/parents";
	private final String USER = "/{userSuperapp}/{userEmail}";
	private String userUrl;
	private int port;
	private UserBoundary superappUser;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void setup() {
		this.restTemplate = new RestTemplate();
		this.baseUrl = "http://localhost:" + this.port + "/superapp/objects";
		this.userUrl = "http://localhost:" + this.port + "/superapp/users";
		NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);
	}

	@BeforeEach
	@AfterEach
	public void tearDown() {
		this.restTemplate.delete("http://localhost:" + this.port + "/superapp/admin/objects");
		this.restTemplate.delete("http://localhost:" + this.port + "/superapp/admin/users");
	}

	@Test
	@DisplayName("test relationship setting between objects")
	public void testRelationshipSettingBetweenObjectsWithSuperAppUser() throws Exception {
		// GIVEN the database already contains 2 objects and the user is super app user

		ObjectId parent = postSuperAppObject("netanelhabas@gmail.com");
		ObjectId child1 = postSuperAppObject("tesleron@gmail.com");
		ObjectId child2 = postSuperAppObject("ormessing@gmail.com");


		SuperAppObjectIdBoundary newObjectIdBoundaryParent = new SuperAppObjectIdBoundary();
		newObjectIdBoundaryParent.setSuperapp(parent.getSuperapp());
		newObjectIdBoundaryParent.setInternalObjectId(parent.getInternalObjectId());

		SuperAppObjectIdBoundary newObjectIdBoundary1 = new SuperAppObjectIdBoundary();
		newObjectIdBoundary1.setSuperapp(child1.getSuperapp());
		newObjectIdBoundary1.setInternalObjectId(child1.getInternalObjectId());

		SuperAppObjectIdBoundary newObjectIdBoundary2 = new SuperAppObjectIdBoundary();
		newObjectIdBoundary2.setSuperapp(child2.getSuperapp());
		newObjectIdBoundary2.setInternalObjectId(child2.getInternalObjectId());

		// WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children

		/// parent will have 2 children : child1, child2

		this.restTemplate.put(this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN + USER, newObjectIdBoundary1,
				parent.getSuperapp(), parent.getSuperapp() + "_" + parent.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());

		this.restTemplate.put(this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN + USER, newObjectIdBoundary2,
				parent.getSuperapp(), parent.getSuperapp() + "_" + parent.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());

		/// child1 will have 2 parents : child2, parent

		this.restTemplate.put(this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN + USER, newObjectIdBoundary1,
				child2.getSuperapp(), child2.getSuperapp() + "_" + child2.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());

		// THEN a relationship will be created between messages
		SuperAppObjectBoundary[] arrParent = this.restTemplate.getForObject(
				this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN + USER, SuperAppObjectBoundary[].class,
				parent.getSuperapp(), parent.getSuperapp() + "_" + parent.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		assertThat(arrParent).isNotEmpty().hasSize(2);

		SuperAppObjectBoundary[] arrChild = this.restTemplate.getForObject(
				this.baseUrl + "/{superapp}/{internalObjectId}" + PARENTS + USER, SuperAppObjectBoundary[].class,
				child1.getSuperapp(), child1.getSuperapp() + "_" + child1.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		assertThat(arrChild).isNotEmpty().hasSize(2);
	}
	

	@Test
	public void testNonExistingParents() throws Exception {
		// GIVEN the database contains two non related messages
		ObjectId parent = postSuperAppObject("netanelhabas@gmail.com");
		ObjectId child = postSuperAppObject("tesleron@gmail.com");

		// WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/parents
		// THEN the server responds with 2xx status
		SuperAppObjectBoundary[] arr = this.restTemplate.getForObject(
				this.baseUrl + "/{superapp}/{internalObjectId}" + PARENTS + USER, SuperAppObjectBoundary[].class,
				parent.getSuperapp(), parent.getSuperapp() + "_" + parent.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(),this.superappUser.getUserId().getEmail());

		assertThat(arr).isEmpty();

	}

	@Test
	public void testNonExistingChildren() throws Exception {
		// GIVEN the database contains two non related messages
		ObjectId parent = postSuperAppObject("netanelhabas@gmail.com");
		ObjectId child = postSuperAppObject("tesleron@gmail.com");

		// WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children
		// THEN the server responds with 2xx status
		SuperAppObjectBoundary[] arr = this.restTemplate.getForObject(
				this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN + USER, SuperAppObjectBoundary[].class,
				parent.getSuperapp(), parent.getSuperapp() + "_" + parent.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(),this.superappUser.getUserId().getEmail());
		assertThat(arr).isEmpty();
	}

	/**
	 * This function post a SuperAppObjectBoundary and creates ObjectId
	 * 
	 * @return ObjectId
	 */
	private ObjectId postSuperAppObject(String email) {
		SuperAppObjectBoundary newSuperAppObjectBoundary = createObjectBoundary(email);
		SuperAppObjectBoundary actual = this.restTemplate.postForObject(this.baseUrl, newSuperAppObjectBoundary,
				SuperAppObjectBoundary.class);
		return actual.getObjectId();

	}

	/**
	 * This function creates a new SuperAppObjectBoundary
	 * 
	 * @return SuperAppObjectBoundary
	 */

	private SuperAppObjectBoundary createObjectBoundary(String email) {
		SuperAppObjectBoundary newSuperAppObjectBoundary = new SuperAppObjectBoundary();
		newSuperAppObjectBoundary.setAlias("test");
		newSuperAppObjectBoundary.setType("student");
		newSuperAppObjectBoundary.setActive(true);
		newSuperAppObjectBoundary.setLocation(new Location(31.4, 31.5));
		newSuperAppObjectBoundary.setCreatedBy(new CreatedBy(new UserId(email)));
		newSuperAppObjectBoundary.setObjectDetails(new HashMap<>());
		return newSuperAppObjectBoundary;
	}

	/**
	 * Create a custom NewUserBoundary object.
	 * 
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
	 * Post New User to DB
	 * 
	 * @param newUserBoundary the new user
	 * @return the UserBoundary created as a result of CRUD Post method
	 */
	private UserBoundary postNewUserToDB(NewUserBoundary newUserBoundary) {
		return this.restTemplate.postForObject(this.userUrl, newUserBoundary, UserBoundary.class);
	}

}
