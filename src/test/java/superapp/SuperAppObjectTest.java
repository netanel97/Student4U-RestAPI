package superapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import net.bytebuddy.asm.Advice.This;
import superapp.entities.CreatedBy;
import superapp.entities.Location;
import superapp.entities.ObjectId;
import superapp.entities.SuperAppObjectBoundary;
import superapp.entities.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SuperAppObjectTest {
	private RestTemplate restTemplate;
	private String baseUrl;
	private String deleteUrl;
	private int port;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void setup() {
		this.restTemplate = new RestTemplate();
		this.baseUrl = "http://localhost:" + this.port + "/superapp/objects";
		this.deleteUrl = "http://localhost:" + this.port + "/superapp/admin/objects";
	}

	@BeforeEach
	@AfterEach
	public void tearDown() {
		this.restTemplate.delete("http://localhost:" + this.port + "/superapp/admin/objects");
	}

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
		ObjectId objectId = postSuperAppObject();
		// THEN the database contains a single object boundary with the content "test"
		assertThat(this.restTemplate.getForObject(this.baseUrl + "/{supperapp}/{internalObjectId}",
				SuperAppObjectBoundary.class, objectId.getSuperApp(), objectId.getInternalObjectId())).isNotNull()
				.extracting("alias").isEqualTo("test");

	}

	/**
	 * This function post a SuperAppObjectBoundary and creates ObjectId
	 * 
	 * @return ObjectId
	 */
	// TODO: need to ask Eyal about try catch
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
		newSuperAppObjectBoundary.setLocation(new Location(31.4, 31.5));
		newSuperAppObjectBoundary.setCreatedBy(new CreatedBy(new UserId("netanelhabas@gmail.com")));
		newSuperAppObjectBoundary.setObjectDetails(new HashMap<>());
		return newSuperAppObjectBoundary;

	}

	@Test
	public void testSuccessPut() {
		/**
		 * GIVEN the server is up AND the database is contains the specific object
		 * requested WHEN I PUT /superapp/objects/{superapp}/{internalObjectId} with
		 * http://localhost:8084/supperapp/objects/2023b.liran.Sorokin/01fa1383-ab45-41a5-8fd2-76585ad6217d
		 * { “type”:”put”, “alias”:”barca” } Then the specific object gets updated.
		 */

		ObjectId objectId = postSuperAppObject();
		// THEN the database contains a single object boundary with the content "test"
		SuperAppObjectBoundary superAppObjectBoundary = this.restTemplate.getForObject(
				this.baseUrl + "/{supperapp}/{internalObjectId}", SuperAppObjectBoundary.class, objectId.getSuperApp(),
				objectId.getInternalObjectId());
		assertThat(superAppObjectBoundary).isNotNull().extracting("objectId").extracting("internalObjectId")
				.isEqualTo(objectId.getInternalObjectId());
		superAppObjectBoundary.setAlias("put");
		superAppObjectBoundary.setType("barca");
		this.restTemplate.put(this.baseUrl + "/{supperapp}/{internalObjectId}", superAppObjectBoundary,
				superAppObjectBoundary.getObjectId().getSuperApp(),
				superAppObjectBoundary.getObjectId().getInternalObjectId());
		assertThat(this.restTemplate.getForObject(this.baseUrl + "/{supperapp}/{internalObjectId}",
				SuperAppObjectBoundary.class, objectId.getSuperApp(), objectId.getInternalObjectId())).isNotNull()
				.extracting("alias").isEqualTo("put");
		assertThat(this.restTemplate.getForObject(this.baseUrl + "/{supperapp}/{internalObjectId}",
				SuperAppObjectBoundary.class, objectId.getSuperApp(), objectId.getInternalObjectId()))
				.extracting("type").isEqualTo("barca");
	}

	@Test
	public void testSuccessEmptyGetAll() {

		/**
		 * GIVEN the server is up AND the database is empty
		 * 
		 * WHEN I GET /superapp/objects
		 * 
		 * Then i get an empty array
		 * 
		 */
		SuperAppObjectBoundary[] arr = this.restTemplate.getForObject(this.baseUrl, SuperAppObjectBoundary[].class);
		assertThat(arr).isNotNull().isEmpty();
	}

	@Test
	public void testSuccessGetAll() {

		/**
		 * GIVEN the server is up AND the database is not empty
		 * 
		 * WHEN I GET /superapp/objects
		 * 
		 * Then i get all objects
		 * 
		 */
		postSuperAppObject();
		postSuperAppObject();
		SuperAppObjectBoundary[] arr = this.restTemplate.getForObject(this.baseUrl, SuperAppObjectBoundary[].class);
		assertThat(arr).isNotNull().isNotEmpty().hasSize(2);
	}

	@Test
	public void testSuccessDeleteAll() {
		/**
		 * GIVEN the server is up AND the database is empty / not empty
		 * 
		 * WHEN i DELETE /superapp/admin/objects Then i delete all objects from DB
		 **/

		ObjectId objectId = postSuperAppObject();
		assertThat(this.restTemplate.getForObject(this.baseUrl + "/{supperapp}/{internalObjectId}",
				SuperAppObjectBoundary.class, objectId.getSuperApp(), objectId.getInternalObjectId())).isNotNull()
				.extracting("objectId").extracting("internalObjectId").isEqualTo(objectId.getInternalObjectId());
		this.restTemplate.delete(this.deleteUrl);
		SuperAppObjectBoundary[] arr = this.restTemplate.getForObject(this.baseUrl, SuperAppObjectBoundary[].class);
		assertThat(arr).isNotNull().isEmpty();

	}

}
