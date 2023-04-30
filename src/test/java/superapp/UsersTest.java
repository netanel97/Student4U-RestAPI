package superapp;

import static org.assertj.core.api.Assertions.assertThat;

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
		this.baseAdminUrl = "http://localhost:" + this.port + "/superapp/admin/users";
	}

//	@AfterEach
//	public void tearDown () {
//		this.restTemplate
//			.delete("http://localhost:" + this.port +"/superapp/admin/users");
//	}

//	@Test
//	public void testUsers() throws Exception {
//		testTheDatabaseIsCleanOnStartup();
//		testSuccessfullPost();
//		testSuccessfullPostUsingSpecificUserGet();
//		testSuccessGetAll();
//	}
	
	@Test
	public void testTheDatabaseIsCleanOnStartup() throws Exception {
		/*
		 * GIVEN the server is up
		 *
		 * WHEN I GET /message
		 */
		UserBoundary[] allUsersInDatabase = this.restTemplate
				.getForObject(this.baseAdminUrl, UserBoundary[].class);

		/*
		 * THEN the server responds with status 2xx AND the server returns empty array
		 */
		assertThat(allUsersInDatabase)
		.isNotNull()
		.isEmpty();

	}

	@Test
	public void testSuccessfullPost() {
		/*
		 * GIVEN the server is up 
		 * AND the database is empty 
		 * WHEN I POST with http://localhost:8084/supperapp/users 
		 * {
		 * 		"email": "Liran@gmail.com",
		 * 		"role":"STUDENT",
		 * 		"avatar":"abc",
		 * 		"username":"Liran"
		 * }
		 */
		NewUserBoundary newUserBoundary = createNewUserBoundary();

		UserBoundary actualUser = this.restTemplate.postForObject(this.baseUrl, newUserBoundary, UserBoundary.class);

		String url = this.baseUrl + "/login/" + springApplicationName + "/" + actualUser.getUserId().getEmail();
//
//		
		// THEN the database contains a single message with the content "hello"
		assertThat(this.restTemplate
			.getForObject(url, UserBoundary.class))
			.isNotNull()
			.extracting("userId.email")
			.isEqualTo(actualUser.getUserId().getEmail());
		
		assertThat(this.restTemplate
			.getForObject(url, UserBoundary.class))
			.isNotNull()
			.extracting("avatar")
			.isEqualTo(actualUser.getAvatar());
		
		assertThat(this.restTemplate
			.getForObject(url, UserBoundary.class))
			.isNotNull()
			.extracting("username")
			.isEqualTo(actualUser.getUsername());

	}

	@Test
	public void testSuccessfullPostUsingSpecificUserGet() {
		/*
		 * GIVEN the server is up 
		 * AND the database is empty 
		 * WHEN I POST http://localhost:8084/superapp/users 
		 * {
		 * 		"avatar":"J",
		 * 		"email":"Liran@gmail.com",
		 * 		"username":"Liran",
		 *		"role":"MINIAPP_USER"
		 *} 
		 */
		NewUserBoundary newUserBoundary = createNewUserBoundary();
		UserBoundary actualUser = this.restTemplate
				.postForObject(this.baseUrl, newUserBoundary, UserBoundary.class);

		UserId userId = actualUser.getUserId();
		String url = this.baseUrl + "/login/" + springApplicationName + "/" + userId.getEmail();

		// THEN the database contains a single user with the email "Liran@gmail.com"
		assertThat(this.restTemplate
				.getForObject(url, UserBoundary.class, actualUser.getUserId()))
		.isNotNull()
		.extracting("userId")
		.extracting("email")
		.isEqualTo("Liran@gmail.com");

	}

	@Test
	public void testSuccessGetAll() {
		/*
		 * GIVEN the server is up AND the database is empty\not empty WHEN I GET
		 * /superapp/admin/users
		 *
		 * Then i get all objects
		 *
		 */
		UserBoundary[] arr = this.restTemplate.getForObject(this.baseAdminUrl, UserBoundary[].class);
		assertThat(arr)
		.isNotNull();

	}

	private NewUserBoundary createNewUserBoundary() {
		NewUserBoundary newUserBoundary = new NewUserBoundary();
		newUserBoundary.setAvatar("K");
		newUserBoundary.setEmail("Liran@gmail.com");
		newUserBoundary.setRole("MINIAPP_USER");
		newUserBoundary.setUsername("Liran");

		return newUserBoundary;

	}

}