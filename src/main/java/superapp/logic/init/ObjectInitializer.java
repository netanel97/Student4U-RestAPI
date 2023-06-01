package superapp.logic.init;

import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import superapp.boundaries.object.CreatedBy;
import superapp.boundaries.object.Location;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserId;
import superapp.data.UserRole;
import superapp.logic.ObjectServiceWithPaginationSupport;
import superapp.logic.UsersService;
import superapp.logic.mongo.ObjectsServiceMongoDb;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@Profile("ObjectInitializer")
public class ObjectInitializer implements CommandLineRunner {

    private ObjectServiceWithPaginationSupport objectServiceWithPaginationSupport;
    private UsersService usersService;
    private String dummyObjectType;

    private String applicationName;
    private String email;
    private UserId userId = null;

    private Log logger = LogFactory.getLog(ObjectInitializer.class);


    @Autowired
    public ObjectInitializer(ObjectServiceWithPaginationSupport objectServiceWithPaginationSupport) {
        super();
        this.objectServiceWithPaginationSupport = objectServiceWithPaginationSupport;
    }
    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Value("${miniapp.command.targetObject.type}")
    public void setDummyObjectType(String dummyObjectType) {
        logger.trace("setDummyObjectType: " + dummyObjectType);
        this.dummyObjectType = dummyObjectType;
    }
    @Value("${spring.application.name}")
    public void setSuperappName(String applicationName) {
        logger.trace("setSuperappName: " + applicationName);
        this.applicationName = applicationName;
    }
    @Value("${dummy.user.email}")
    public void setEmail(String email) {
        logger.trace("setEmail: " + email);
        this.email = email;
    }

    @PostConstruct
    public void init() {
        this.userId = new UserId(this.email);
        this.userId.setSuperapp(this.applicationName);
        logger.trace("init: " + this.userId);
    }


    @Override
    public void run(String... args) throws Exception {

        createDummyObject();
    }

    private void createDummyObject() {
        logger.trace("Entering createDummyObject");
        loginOrCreateDummyObject();
        List<SuperAppObjectBoundary> dummyObjects =  objectServiceWithPaginationSupport.searchObjectsByType(this.applicationName, this.email, this.dummyObjectType, 1, 0);
        if (dummyObjects.size() == 0) {
            logger.trace("Dummy object not found, creating new dummy object....");
            createNewDummyObject();
        }
        else {
        }
    }
    private void createNewDummyObject() {
        logger.trace("Entering createNewDummyObject");
        SuperAppObjectBoundary object = new SuperAppObjectBoundary();
        object.setType(this.dummyObjectType);
        object.setCreatedBy(new CreatedBy(this.userId));
        object.setAlias("dummyObject");
        object.setLocation(new Location(0.0, 0.0));
        object.setActive(true);
        object.setObjectDetails(new HashMap<>());
        logger.trace("Creating new dummy object: " + object);
        objectServiceWithPaginationSupport.createObject(object);
    }


    private void loginOrCreateDummyObject() {
        logger.trace("Entering loginOrCreateDummyObject");
        Optional<UserBoundary> userDummyObject = usersService.login(this.applicationName, this.email);
        if(userDummyObject.isEmpty()){
            logger.trace("User not found, creating new user....");
            UserBoundary newUserDummyObject;
            UserId id = new UserId(this.email);
            id.setSuperapp(this.applicationName);
            newUserDummyObject = new UserBoundary(id, UserRole.SUPERAPP_USER.toString(), "DummyObject", "None");
            logger.trace("Creating new user: " + newUserDummyObject);
            usersService.createUser(newUserDummyObject);
            logger.trace("Exiting from loginOrCreateDummyObject function");


        }
    }
}
