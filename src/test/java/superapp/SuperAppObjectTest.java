package superapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.object.CreatedBy;
import superapp.boundaries.object.Location;
import superapp.boundaries.object.ObjectId;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SuperAppObjectTest {
	private RestTemplate restTemplate;
	private String baseUrl;
	private String deleteUrl;
	private int port;
	private final String DELIMITER = "_";
	private UserBoundary superappUser;
	private String userUrl;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void setup() {
		this.restTemplate = new RestTemplate();
		this.baseUrl = "http://localhost:" + this.port + "/superapp/objects";
		this.deleteUrl = "http://localhost:" + this.port + "/superapp/admin/objects";
		this.userUrl = "http://localhost:" + this.port + "/superapp/users";
	}

	@BeforeEach
	@AfterEach
	public void tearDown() {
		NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "ADMIN", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);
		this.restTemplate.delete(this.deleteUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}", this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
	}



	// TODO: need to check active and permission if not
	@Test
	public void testSuccessfullPostUsingSpecificSuperappObjectGet() {

		/**
		 * GIVEN the server is up AND the database is empty
		 * 
		 * WHEN I POST /superapp/objects with
		 * 
		 * “objectId”:{ “superapp”: “2023b.LiranSorokin”, “internalObjectId”:”53” },
		 * “type”:”student”, “alias”:”neta”, “active”:true,
		 * “creationTimestamp”:”2023-04-15T15:01:40.209+03:00”, “location”:{ “lat”:32.2,
		 * “lon”:31.2 }, “createdBy”: { “userId”: { “superApp”: “2023b.Liran.Sorokin”,
		 * “email”: “netanelhabas@gmail.com” } }, “objectDetails”:{}
		 * 
		 */
		NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);

		ObjectId objectId = postSuperAppObject();
		// THEN the database contains a single object boundary with the content "test"
		// TODO: check with user permission and without user permission
		SuperAppObjectBoundary superAppObjectBoundary = this.restTemplate.getForObject(
				this.baseUrl + "/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary.class, objectId.getSuperapp(), objectId.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		assertThat(superAppObjectBoundary).isNotNull().extracting("objectId.internalObjectId").isEqualTo(objectId.getInternalObjectId());
	}


	@Test
	public void testSuccessfullPostDummyObject(){
		/**
		 * GIVEN the server is up AND the database have user with the permission "SUPERAPP_USER"
		 * WHEN I POST /superapp/objects with
		 *
		 * 		 * “objectId”:{ “superapp”: “2023b.LiranSorokin”, “internalObjectId”:”UUID” },
		 * 		 * “type”:”dummyObject”, “alias”:”dummyObject”, “active”:true,
		 * 		 * “creationTimestamp”:”2023-04-15T15:01:40.209+03:00”, “location”:{ “lat”:0.0,
		 * 		 * “lon”:0.0 }, “createdBy”: { “userId”: { “superApp”: “2023b.Liran.Sorokin”,
		 * 		 * “email”: “dummy@gmail.com” } }, “objectDetails”:{}
		 */
		NewUserBoundary newUserBoundary = createNewUserBoundary("dummy@gmail.com", "DummyObject", "SUPERAPP_USER", "None");
		this.superappUser = postNewUserToDB(newUserBoundary);
		ObjectId objectId = postDummyObject();
		SuperAppObjectBoundary superAppObjectBoundary = this.restTemplate.getForObject(
				this.baseUrl + "/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary.class, objectId.getSuperapp(), objectId.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		System.err.println(superAppObjectBoundary);
		assertThat(superAppObjectBoundary).isNotNull().extracting("objectId.internalObjectId").isEqualTo(objectId.getInternalObjectId());




		// THEN the database contains a single object boundary with the dummy object

	}

	private ObjectId postDummyObject() {
		SuperAppObjectBoundary newSuperAppObjectBoundary = new SuperAppObjectBoundary();
		newSuperAppObjectBoundary.setAlias("dummyObject");
		newSuperAppObjectBoundary.setType("dummyObject");
		newSuperAppObjectBoundary.setActive(true);
		newSuperAppObjectBoundary.setCreationTimestamp(new Date());
		newSuperAppObjectBoundary.setLocation(new Location(0.0, 0.0));
		newSuperAppObjectBoundary.setCreatedBy(new CreatedBy(this.superappUser.getUserId()));
		newSuperAppObjectBoundary.setObjectDetails(new HashMap<>());
		SuperAppObjectBoundary actual = this.restTemplate.postForObject(this.baseUrl, newSuperAppObjectBoundary,
				SuperAppObjectBoundary.class);
		return actual.getObjectId();
	}


	/**
	 * This function post a SuperAppObjectBoundary and creates ObjectId
	 * 
	 * @return ObjectId
	 */
	private ObjectId postSuperAppObject() {


		SuperAppObjectBoundary newSuperAppObjectBoundary = createObjectBoundary();
		SuperAppObjectBoundary actual = this.restTemplate.postForObject(this.baseUrl, newSuperAppObjectBoundary,
				SuperAppObjectBoundary.class);
		return actual.getObjectId();
	}

	/**
	 * This function creates a new SuperAppObjectBoundary
	 * 
	 * @return SuperAppObjectBoundary
	 */
	private SuperAppObjectBoundary createObjectBoundary() {
		SuperAppObjectBoundary newSuperAppObjectBoundary = new SuperAppObjectBoundary();
		newSuperAppObjectBoundary.setAlias("test");
		newSuperAppObjectBoundary.setType("student");
		newSuperAppObjectBoundary.setActive(true);
		newSuperAppObjectBoundary.setCreationTimestamp(new Date());
		newSuperAppObjectBoundary.setLocation(new Location(31.4, 31.5));
		newSuperAppObjectBoundary.setCreatedBy(new CreatedBy(new UserId("adam@gmail.com")));
		newSuperAppObjectBoundary.setObjectDetails(new HashMap<>());
		return newSuperAppObjectBoundary;
	}

	// TODO: need to check permission
	@Test
	public void testSuccessPut() {
		NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);

		/**
		 * GIVEN the server is up AND the database is contains the specific object
		 * requested WHEN I PUT /superapp/objects/{superapp}/{internalObjectId} with
		 * http://localhost:8084/superapp/objects/2023b.liran.Sorokin/01fa1383-ab45-41a5-8fd2-76585ad6217d
		 * { “type”:”put”, “alias”:”barca” } Then the specific object gets updated.
		 */

		ObjectId objectId = postSuperAppObject();
		// THEN the database contains a single object boundary with the content "test"
		SuperAppObjectBoundary superAppObjectBoundary = this.restTemplate.getForObject(
				this.baseUrl + "/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary.class, objectId.getSuperapp(), objectId.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		assertThat(superAppObjectBoundary).isNotNull().extracting("objectId").extracting("internalObjectId")
				.isEqualTo(objectId.getInternalObjectId());
		superAppObjectBoundary.setAlias("put");
		superAppObjectBoundary.setType("barca");
		this.restTemplate.put(
		this.baseUrl + "/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
		superAppObjectBoundary, superAppObjectBoundary.getObjectId().getSuperapp(),
		superAppObjectBoundary.getObjectId().getInternalObjectId(), this.superappUser.getUserId().getSuperapp(),
		this.superappUser.getUserId().getEmail());
		assertThat(this.restTemplate.getForObject(this.baseUrl + "/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
		SuperAppObjectBoundary.class, objectId.getSuperapp(), objectId.getInternalObjectId(),
		this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail())).isNotNull()
				.extracting("alias").isEqualTo("put");
		assertThat(this.restTemplate.getForObject(this.baseUrl + "/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary.class, objectId.getSuperapp(), objectId.getInternalObjectId(),
				this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail())).isNotNull()
				.extracting("type").isEqualTo("barca");
	}

	// TODO: need to check active and permission if not
	@Test
	public void testSuccessEmptyGetAll() {
		NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);

		/**
		 * GIVEN the server is up AND the database is empty
		 * 
		 * WHEN I GET /superapp/objects
		 * 
		 * Then i get an empty array
		 * 
		 */

		SuperAppObjectBoundary[] arr = this.restTemplate.getForObject(
				this.baseUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary[].class, this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		assertThat(arr).isNotNull().isEmpty();
	}

	// TODO: need to check active and permission if not
	@Test
	public void testSuccessGetAll() {
		NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);

		/**
		 * GIVEN the server is up AND the database is not empty
		 * 
		 * WHEN I GET /superapp/objects?userSuperapp={userSuperapp}&userEmail={userEmail}&size={size}&page={page}
		 * 
		 * Then i get all objects
		 * 
		 */
		postSuperAppObject();
		postSuperAppObject();
		SuperAppObjectBoundary[] arr = this.restTemplate.getForObject(
				this.baseUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary[].class, this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		assertThat(arr).isNotEmpty().hasSize(2);
	}

	@Test
	public void testSuccessDeleteAll() {
		NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);
		/**
		 * GIVEN the server is up AND the database is empty / not empty
		 * 
		 * WHEN i DELETE /superapp/admin/objects Then i delete all objects from DB
		 **/

		ObjectId objectId = postSuperAppObject();

		newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);

		assertThat(this.restTemplate.getForObject(
		this.baseUrl + "/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
		SuperAppObjectBoundary.class, objectId.getSuperapp(), objectId.getInternalObjectId(),
		this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail())).isNotNull()
		.extracting("objectId").extracting("internalObjectId").isEqualTo(objectId.getInternalObjectId());

		newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "ADMIN", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);

		this.restTemplate.delete(this.deleteUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}", this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
		this.superappUser = postNewUserToDB(newUserBoundary);

		SuperAppObjectBoundary[] arr = this.restTemplate.getForObject(
				this.baseUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary[].class, this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
		assertThat(arr).isNotNull().isEmpty();
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
