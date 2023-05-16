package superapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import superapp.entities.CommandId;
import superapp.entities.InvokedBy;
import superapp.entities.MiniAppCommandBoundary;
import superapp.entities.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MiniAppCommandTest {

	private RestTemplate restTemplate;
	private String baseUrl;
	private String deleteOrGetUrl;
	private int port;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void setup() {
		this.restTemplate = new RestTemplate();
		this.baseUrl = "http://localhost:" + this.port + "/superapp/miniapp";
		this.deleteOrGetUrl = "http://localhost:" + this.port + "/superapp/admin/miniapp";
	}

	@AfterEach
	public void tearDown() {
		this.restTemplate.delete("http://localhost:" + this.port + "/superapp/admin/miniapp");
	}
	
	@Test
	public void testSuccessfullPostUsingSpecificMiniAppCommandGet() {
		/**
		 * GIVEN the server is up AND the database is empty
		 * 
		 * WHEN I POST /superapp/miniapp/{miniAppName} with
		 * 
		 * “type”:”student”, “alias”:”neta”, “active”:true,
		 * “location”:{ “lat”:32.2,
		 * “lon”:31.2 }, “createdBy”:{ “userId”:{ “superApp”: “2023b.Liran.Sorokin”,
		 * “email”: “netanelhabas@gmail.com” } }, “objectDetails”:{}
		 * 
		 */
		CommandId commandId = postMiniAppCommand();
		// THEN the database contains a single object boundary with the content "test"
		
		MiniAppCommandBoundary[] arr = this.restTemplate.getForObject(this.deleteOrGetUrl + "/{miniAppName}", MiniAppCommandBoundary[].class, commandId.getMiniapp());
		assertThat(arr).isNotNull().isNotEmpty().hasSize(1);

	}
	/**
	 * This function post a MiniAppCommandBoundary and creates CommandId
	 * @return CommandId
	 */
	private CommandId postMiniAppCommand() {
		MiniAppCommandBoundary newMiniAppCommandBoundary = createMiniAppCommandBoundary();
		MiniAppCommandBoundary actual = this.restTemplate.postForObject(this.baseUrl + "/{miniAppName}" , newMiniAppCommandBoundary,
				MiniAppCommandBoundary.class, newMiniAppCommandBoundary.getCommandId().getMiniapp());

		return actual.getCommandId();

	}
	
	private MiniAppCommandBoundary createMiniAppCommandBoundary() {
		MiniAppCommandBoundary newMiniAppCommandBoundary = new MiniAppCommandBoundary();
		
		newMiniAppCommandBoundary.setCommand("test");
		newMiniAppCommandBoundary.setInvokedBy(new InvokedBy(new UserId("netanelhabas@gmail.com")));
		newMiniAppCommandBoundary.setTargetObject(null);
		newMiniAppCommandBoundary.setCommandId(new CommandId("someMiniApp", "123"));
		newMiniAppCommandBoundary.setInvocationTimestamp(null);
		newMiniAppCommandBoundary.setCommandAttributes(new HashMap<>());
		return newMiniAppCommandBoundary;

	}
	
	@Test
	public void testSuccessEmptyGetAllCommands() {

		/**
		 * GIVEN the server is up AND the database is empty
		 * 
		 * WHEN I GET /superapp/admin/miniapp
		 * 
		 * Then i get an empty array
		 * 
		 */
		MiniAppCommandBoundary[] arr = this.restTemplate.getForObject(this.deleteOrGetUrl, MiniAppCommandBoundary[].class);
		assertThat(arr).isNotNull().isEmpty();
	}

	@Test
	public void testSuccessGetAllCommands() {

		/**
		 * GIVEN the server is up AND the database is not empty
		 * 
		 * WHEN I GET /superapp/admin/miniapp
		 * 
		 * Then i get all commands history
		 * 
		 */
		postMiniAppCommand();
		postMiniAppCommand();
		MiniAppCommandBoundary[] arr = this.restTemplate.getForObject(this.deleteOrGetUrl, MiniAppCommandBoundary[].class);
		assertThat(arr).isNotNull().isNotEmpty().hasSize(2);
	}
	
	@Test
	public void testSuccessDeleteAllCommands() {
		/**
		 * GIVEN the server is up AND the database is empty / not empty
		 * 
		 * WHEN i DELETE /superapp/admin/miniapp Then i delete all commands history from DB
		 **/

		CommandId commandId = postMiniAppCommand();
		MiniAppCommandBoundary[] arr = this.restTemplate.getForObject(this.deleteOrGetUrl, MiniAppCommandBoundary[].class);
		assertThat(arr).isNotNull().isNotEmpty().hasSize(1);
		this.restTemplate.delete(this.deleteOrGetUrl);
		arr = this.restTemplate.getForObject(this.deleteOrGetUrl, MiniAppCommandBoundary[].class);
		assertThat(arr).isNotNull().isEmpty();

	}
	
	@Test
	public void testSuccessGetAllSpecificMiniAppCommands() {

		/**
		 * GIVEN the server is up AND the database is not empty
		 * 
		 * WHEN I GET /superapp/admin/miniapp/{miniAppName}
		 * 
		 * Then i get all objects
		 * 
		 */
		CommandId commandId = postMiniAppCommand();
		MiniAppCommandBoundary[] specificCommands = this.restTemplate.getForObject(this.deleteOrGetUrl + "/{miniAppName}", MiniAppCommandBoundary[].class, commandId.getMiniapp());
		assertThat(specificCommands).isNotNull().isNotEmpty().hasSize(1);
	}

	
	
}