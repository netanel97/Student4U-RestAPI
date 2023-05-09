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
import superapp.entities.CreatedBy;
import superapp.entities.Location;
import superapp.entities.ObjectId;
import superapp.entities.SuperAppObjectBoundary;
import superapp.entities.SuperAppObjectIdBoundary;
import superapp.entities.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SuperAppObjectRelationshipsTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private final String CHILDREN = "/children";
	private final String PARENTS = "/parents";
	private int port;
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void setup() {
		this.restTemplate = new RestTemplate();
		this.baseUrl = "http://localhost:" + this.port + "/superapp/objects";

	}

	@BeforeEach
	@AfterEach
	public void tearDown() {
		this.restTemplate.delete("http://localhost:" + this.port + "/superapp/admin/objects");
	}
	
	@Test
	@DisplayName("test relationship setting between objects")
	public void testRelationshipSettingBetweenObjects() throws Exception{
		// GIVEN the database already contains 2 objects
		ObjectId parent = postSuperAppObject("netanelhabas@gmail.com");
		ObjectId child1 = postSuperAppObject("tesleron@gmail.com");
		ObjectId child2 = postSuperAppObject("ormessing@gmail.com");
		
		SuperAppObjectIdBoundary newObjectIdBoundaryParent = new SuperAppObjectIdBoundary();
		newObjectIdBoundaryParent.setSuperapp(parent.getSuperApp());
		newObjectIdBoundaryParent.setInternalObjectId(parent.getInternalObjectId());
		
		SuperAppObjectIdBoundary newObjectIdBoundary1 = new SuperAppObjectIdBoundary();
		newObjectIdBoundary1.setSuperapp(child1.getSuperApp());
		newObjectIdBoundary1.setInternalObjectId(child1.getInternalObjectId());
		
		SuperAppObjectIdBoundary newObjectIdBoundary2 = new SuperAppObjectIdBoundary();
		newObjectIdBoundary2.setSuperapp(child2.getSuperApp());
		newObjectIdBoundary2.setInternalObjectId(child2.getInternalObjectId());
		
		
		// WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children
		
		/// parent will have 2 children : child1, child2
		
		this.restTemplate
			.put(this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN, 
					newObjectIdBoundary1, 
					parent.getSuperApp(), parent.getSuperApp() + "_" + parent.getInternalObjectId());
		
		this.restTemplate
			.put(this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN, 
					newObjectIdBoundary2, 
					parent.getSuperApp(), parent.getSuperApp() + "_" + parent.getInternalObjectId());
		
		/// child1 will have 2 parents : child2, parent 
		
		this.restTemplate
		.put(this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN, 
				newObjectIdBoundary1, 
				child2.getSuperApp(), child2.getSuperApp() + "_" + child2.getInternalObjectId());
		
		
		// THEN a relationship will be created between messages
		SuperAppObjectBoundary[] arrParent = this.restTemplate.getForObject(this.baseUrl+ "/{superapp}/{internalObjectId}" + CHILDREN, SuperAppObjectBoundary[].class, 
				parent.getSuperApp(), parent.getSuperApp() + "_" + parent.getInternalObjectId());
		assertThat(arrParent).isNotEmpty().hasSize(2);
		
		SuperAppObjectBoundary[] arrChild = this.restTemplate.getForObject(this.baseUrl+ "/{superapp}/{internalObjectId}" + PARENTS, SuperAppObjectBoundary[].class, 
				child1.getSuperApp(), child1.getSuperApp() + "_" + child1.getInternalObjectId());
		assertThat(arrChild).isNotEmpty().hasSize(2);
		
	}
	
	@Test
	public void testNonExistingParents() throws Exception{
		// GIVEN the database contains two non related messages
		ObjectId parent = postSuperAppObject("netanelhabas@gmail.com");
		ObjectId child = postSuperAppObject("tesleron@gmail.com");
		
		// WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/parents
		// THEN the server responds with 2xx status
		SuperAppObjectBoundary[] arr =
				  this.restTemplate
					.getForObject(this.baseUrl + "/{superapp}/{internalObjectId}" + PARENTS, SuperAppObjectBoundary[].class, 
							parent.getSuperApp(), parent.getSuperApp() + "_" + parent.getInternalObjectId());
		
		assertThat(arr).isEmpty();
		
	}
	
	@Test
	public void testNonExistingChildren() throws Exception{
		// GIVEN the database contains two non related messages
		ObjectId parent = postSuperAppObject("netanelhabas@gmail.com");
		ObjectId child = postSuperAppObject("tesleron@gmail.com");
		
		// WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children
		// THEN the server responds with 2xx status
		SuperAppObjectBoundary[] arr =
				  this.restTemplate
					.getForObject(this.baseUrl + "/{superapp}/{internalObjectId}" + CHILDREN, SuperAppObjectBoundary[].class, 
							parent.getSuperApp(), parent.getSuperApp() + "_" + parent.getInternalObjectId());
		
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
	
	
}
