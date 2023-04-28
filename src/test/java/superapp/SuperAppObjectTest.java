package superapp;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
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
		this.baseUrl = "http://localhost:" + this.port + "/superapp/objects";
	}
	
//
//	@AfterEach
//
//	public void tearDown () {
//		this.restTemplate
//			.delete("http://localhost:" + this.port +"/superapp/admin/objects");
//	}


    @Test
    public void testSuccessfullPostUsingSpecificSuperappObjectGet() {
    	/**
         GIVEN the server is up
         AND the database is empty
        
        WHEN I POST /superapp/objects with 
    	
         * “objectId”:{
		“superapp”: “2023b.LiranSorokin”,
		“internalObjectId”:”53”
		},
		“type”:”student”,
		“alias”:”neta”,
		“active”:true,
		“creationTimestamp”:”2023-04-15T15:01:40.209+03:00”,
		“location”:{
			“lat”:32.2,
			“lon”:31.2
			},
		“createdBy”:{
		“userId”:{
			“superApp”: “2023b.Liran.Sorokin”,
			“email”: “netanelhabas@gmail.com”
		}
		},
		“objectDetails”:{}

         */
    	SuperAppObjectBoundary newSuperAppObjectBoundary =createObjectBoundary();
        SuperAppObjectBoundary actual = this.restTemplate
            .postForObject(this.baseUrl, newSuperAppObjectBoundary, SuperAppObjectBoundary.class);
        ObjectId objectId = actual.getObjectId();
        // THEN the database contains a single object boundary with the content "test"
        assertThat(this.restTemplate
                .getForObject(this.baseUrl +"/"+ objectId.getSuperApp() + "/" + objectId.getInternalObjectId(), SuperAppObjectBoundary.class, actual.getObjectId()))
            .isNotNull()
            .extracting("alias")
            .isEqualTo("test");
        
    }
   
    private SuperAppObjectBoundary createObjectBoundary() {
    	SuperAppObjectBoundary newSuperAppObjectBoundary = new SuperAppObjectBoundary();
        newSuperAppObjectBoundary.setAlias("test");
        newSuperAppObjectBoundary.setType("student");
        newSuperAppObjectBoundary.setActive(true);
        newSuperAppObjectBoundary.setLocation(new Location(31.4,31.5));
        newSuperAppObjectBoundary.setCreatedBy(new CreatedBy(new UserId("netanelhabas@gmail.com")));
        newSuperAppObjectBoundary.setObjectDetails(new HashMap<>());
        return newSuperAppObjectBoundary;
    	
    }
    
    @Test
    public void testSuccessfullPost() {
    	/**
    	GIVEN the server is up
        AND the database is contains the specific object requested
    	 WHEN I POST /superapp/objects/{superapp}/{internalObjectId}
    	  with  http://localhost:8084/supperapp/objects/2023b.liran.Sorokin/UUID
    	 * “objectId”:{
		“superapp”: “2023b.LiranSorokin”,
		“internalObjectId”:”53”
		},
		“type”:”student”,
		“alias”:”neta”,
		“active”:true,
		“creationTimestamp”:”2023-04-15T15:01:40.209+03:00”,
		“location”:{
			“lat”:32.2,
			“lon”:31.2
			},
		“createdBy”:{
		“userId”:{
			“superApp”: “2023b.Liran.Sorokin”,
			“email”: “netanelhabas@gmail.com”
		}
		},
		“objectDetails”:{}
    	 */
    	SuperAppObjectBoundary newSuperAppObjectBoundary =createObjectBoundary();
    	  SuperAppObjectBoundary actual = this.restTemplate
    	            .postForObject(this.baseUrl, newSuperAppObjectBoundary, SuperAppObjectBoundary.class);
   
    }
//    @Test
//    public void testSuccessfullPut() {
//       	/**
//    	GIVEN the server is up
//        AND the database is contains the specific object requested
//    	 WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}
//    	  with  http://localhost:8084/supperapp/objects/2023b.liran.Sorokin/01fa1383-ab45-41a5-8fd2-76585ad6217d
//    	{
//			“type”:”put”,
//			“alias”:”barca”
//		}
//		Then the specific object gets updated.
//    	 */
//    	
//    	SuperAppObjectBoundary newSuperAppObjectBoundary = new SuperAppObjectBoundary();
//        newSuperAppObjectBoundary.setAlias("put");
//        newSuperAppObjectBoundary.setType("barca");
//        this.restTemplate.put(this.baseUrl + "/" + this.springApplicationName + "/" + "01fa1383-ab45-41a5-8fd2-76585ad6217d",
//        		newSuperAppObjectBoundary,
//        		SuperAppObjectBoundary.class);
//        assertThat(this.restTemplate
//                .getForObject(this.baseUrl + "/" + this.springApplicationName + "/" + "01fa1383-ab45-41a5-8fd2-76585ad6217d", SuperAppObjectBoundary.class))
//            .isNotNull()
//            .extracting("alias")
//            .isEqualTo("put");
//        assertThat(this.restTemplate
//                .getForObject(this.baseUrl + "/" + this.springApplicationName + "/" + "01fa1383-ab45-41a5-8fd2-76585ad6217d", SuperAppObjectBoundary.class))
//        .extracting("type")
//        .isEqualTo("barca");
//        
//    }
    @Test
    public void testSuccessGetAll() {
    	
    	/**
        GIVEN the server is up
        AND the database is  empty / not empty
       
       WHEN I GET /superapp/objects
       
        Then i get all objects

        */
    	 SuperAppObjectBoundary[] arr = this.restTemplate
                 .getForObject(this.baseUrl, SuperAppObjectBoundary[].class);
    	 assertThat(arr).isNotNull();

    }
   
//    @Test
//    public void testSuccessGetSpecific() {
//    	
//    	/**
//        GIVEN the server is up
//        AND the database contains the specific object requested
//       
//       WHEN I GET /superapp/objects/{superapp}/{internalObjectId}
//       
//        Then i get the specific object
//
//        */
//    	
//    	 assertThat(this.restTemplate
//                 .getForObject(this.baseUrl + "/" + this.springApplicationName + "/" + "01fa1383-ab45-41a5-8fd2-76585ad6217d", SuperAppObjectBoundary.class))
//             .isNotNull()
//             .extracting("objectId")
//             .extracting("superapp")
//             .isEqualTo(this.springApplicationName);
//    	 assertThat(this.restTemplate
//                 .getForObject(this.baseUrl + "/" + this.springApplicationName + "/" + "01fa1383-ab45-41a5-8fd2-76585ad6217d", SuperAppObjectBoundary.class))
//             .isNotNull()
//             .extracting("objectId")
//             .extracting("internalObjectId")
//             .isEqualTo("01fa1383-ab45-41a5-8fd2-76585ad6217d");
//    }
    
    
}
