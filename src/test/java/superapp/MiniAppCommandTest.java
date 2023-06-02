package superapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.command.CommandId;
import superapp.boundaries.command.InvokedBy;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.TargetObject;
import superapp.boundaries.object.CreatedBy;
import superapp.boundaries.object.Location;
import superapp.boundaries.object.ObjectId;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MiniAppCommandTest {

    private RestTemplate restTemplate;
    private String baseUrl;
    private String deleteOrGetUrl;
    private int port;
    private UserBoundary superappUser;
    private String userUrl;
    private String miniAppUrl;

    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
    }

    @PostConstruct
    public void setup() {
        this.restTemplate = new RestTemplate();
        this.baseUrl = "http://localhost:" + this.port + "/superapp/objects";
        this.miniAppUrl = "http://localhost:" + this.port + "/superapp/miniapp";
        this.deleteOrGetUrl = "http://localhost:" + this.port + "/superapp/admin/miniapp";
        this.userUrl = "http://localhost:" + this.port + "/superapp/users";
    }
@BeforeEach
	@AfterEach
	public void tearDown() {
    this.superappUser = postNewUserToDB(createNewUserBoundary("adam@gmail.com", "adam", "ADMIN", "A"));
    this.restTemplate.delete(this.deleteOrGetUrl+ "?userSuperapp={userSuperapp}&userEmail={userEmail}",this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail() );
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
        this.superappUser = postNewUserToDB(createNewUserBoundary("adam@gmail.com", "adam", "ADMIN", "A"));

        MiniAppCommandBoundary[] arr = this.restTemplate.getForObject(this.deleteOrGetUrl + "/{miniAppName}?userSuperapp={userSuperapp}&userEmail={userEmail}", MiniAppCommandBoundary[].class, commandId.getMiniapp(), this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
        assertThat(arr).isNotNull().isNotEmpty().hasSize(1);

    }

    /**
     * This function post a MiniAppCommandBoundary and creates CommandId
     *
     * @return CommandId
     */
    private CommandId postMiniAppCommand() {
        MiniAppCommandBoundary newMiniAppCommandBoundary = createMiniAppCommandBoundary();
        MiniAppCommandBoundary actual = this.restTemplate.postForObject(this.miniAppUrl + "/{miniAppName}", newMiniAppCommandBoundary, MiniAppCommandBoundary.class, newMiniAppCommandBoundary.getCommandId().getMiniapp());

        return actual.getCommandId();

    }

    private MiniAppCommandBoundary createMiniAppCommandBoundary() {

        SuperAppObjectBoundary obj = createObjectBoundary();
        TargetObject target = new TargetObject();
        target.setObjectId(postSuperAppObject());
        postSuperAppObject(obj);
        MiniAppCommandBoundary newMiniAppCommandBoundary = new MiniAppCommandBoundary();
        NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "MINIAPP_USER", "A");
        this.superappUser = postNewUserToDB(newUserBoundary);
        CommandId commandId = new CommandId("123", "test");
        HashMap<String, Object> details = new HashMap<String, Object>();

        newMiniAppCommandBoundary.setCommandId(commandId);
        newMiniAppCommandBoundary.setCommand("command");
        newMiniAppCommandBoundary.setTargetObject(target);
        newMiniAppCommandBoundary.setInvocationTimestamp(new Date());
        newMiniAppCommandBoundary.setInvokedBy(new InvokedBy(superappUser.getUserId()));
        newMiniAppCommandBoundary.setCommandAttributes(details);
        return newMiniAppCommandBoundary;

    }

    private ObjectId postSuperAppObject(SuperAppObjectBoundary obj) {

        NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
        this.superappUser = postNewUserToDB(newUserBoundary);
        SuperAppObjectBoundary actual = this.restTemplate.postForObject(this.baseUrl, obj, SuperAppObjectBoundary.class);
        return actual.getObjectId();
    }

    private ObjectId postSuperAppObject() {

        NewUserBoundary newUserBoundary = createNewUserBoundary("adam@gmail.com", "adam", "SUPERAPP_USER", "A");
        this.superappUser = postNewUserToDB(newUserBoundary);

        SuperAppObjectBoundary newSuperAppObjectBoundary = createObjectBoundary();
        SuperAppObjectBoundary actual = this.restTemplate.postForObject(this.baseUrl, newSuperAppObjectBoundary, SuperAppObjectBoundary.class);
        return actual.getObjectId();
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

    private SuperAppObjectBoundary createObjectBoundary() {
        SuperAppObjectBoundary newSuperAppObjectBoundary = new SuperAppObjectBoundary();
        newSuperAppObjectBoundary.setAlias("test");
        newSuperAppObjectBoundary.setType("student");
        newSuperAppObjectBoundary.setActive(true);
        newSuperAppObjectBoundary.setObjectId(new ObjectId("test"));
        newSuperAppObjectBoundary.setCreationTimestamp(new Date());
        newSuperAppObjectBoundary.setLocation(new Location(31.4, 31.5));
        newSuperAppObjectBoundary.setCreatedBy(new CreatedBy(new UserId("adam@gmail.com")));
        newSuperAppObjectBoundary.setObjectDetails(new HashMap<>());
        return newSuperAppObjectBoundary;
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
        this.superappUser = postNewUserToDB(createNewUserBoundary("adam@gmail.com", "adam", "ADMIN", "A"));
        MiniAppCommandBoundary[] arr = this.restTemplate.getForObject(this.deleteOrGetUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}", MiniAppCommandBoundary[].class, this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
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
        this.superappUser = postNewUserToDB(createNewUserBoundary("adam@gmail.com", "adam", "ADMIN", "A"));
        MiniAppCommandBoundary[] arr = this.restTemplate.getForObject(this.deleteOrGetUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}", MiniAppCommandBoundary[].class, this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
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
        this.superappUser = postNewUserToDB(createNewUserBoundary("adam@gmail.com", "adam", "ADMIN", "A"));
        MiniAppCommandBoundary[] arr = this.restTemplate.getForObject(this.deleteOrGetUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}", MiniAppCommandBoundary[].class, this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());

        assertThat(arr).isNotNull().isNotEmpty().hasSize(1);
        this.restTemplate.delete(this.deleteOrGetUrl+ "?userSuperapp={userSuperapp}&userEmail={userEmail}",this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail() );
        arr = this.restTemplate.getForObject(this.deleteOrGetUrl + "?userSuperapp={userSuperapp}&userEmail={userEmail}", MiniAppCommandBoundary[].class, this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
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
        this.superappUser = postNewUserToDB(createNewUserBoundary("adam@gmail.com", "adam", "ADMIN", "A"));
        MiniAppCommandBoundary[] specificCommands = this.restTemplate.getForObject(this.deleteOrGetUrl + "/{miniAppName}?userSuperapp={userSuperapp}&userEmail={userEmail}", MiniAppCommandBoundary[].class, commandId.getMiniapp(),  this.superappUser.getUserId().getSuperapp(), this.superappUser.getUserId().getEmail());
        assertThat(specificCommands).isNotNull().isNotEmpty().hasSize(1);
    }


}