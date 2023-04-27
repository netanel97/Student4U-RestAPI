package superapp;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
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
    	 WHEN I GET /superapp/objects/{superapp}/{internalObjectId}
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
//    	SuperAppObjectBoundary actual = this.restTemplate.put(this.baseUrl + "/" + this.springApplicationName + "/" + , );
//    	
//    	
//    }
    
    
}
