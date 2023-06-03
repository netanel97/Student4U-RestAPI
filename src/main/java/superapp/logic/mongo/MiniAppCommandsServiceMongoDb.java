package superapp.logic.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.command.CommandId;
import superapp.boundaries.command.InvokedBy;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.TargetObject;
import superapp.boundaries.object.ObjectId;
import superapp.boundaries.user.UserId;
import superapp.dal.MiniAppCommandCrud;
import superapp.dal.SuperAppObjectCrud;
import superapp.dal.UserCrud;
import superapp.data.MiniAppCommandEntity;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.MiniAppCommandNotFoundException;
import superapp.logic.MiniAppCommandsServiceWithPaginationSupport;
import superapp.logic.MiniAppForum;
import superapp.logic.MiniAppGradeAVG;
import superapp.logic.MiniAppService;
import superapp.logic.SuperAppObjectNotActiveException;
import superapp.logic.UnauthorizedAccessException;
import superapp.logic.UserNotFoundException;
import superapp.utils.Constants;
import superapp.utils.MiniAppCommandConverter;
import superapp.utils.ObjectConverter;

@Service
public class MiniAppCommandsServiceMongoDb implements MiniAppCommandsServiceWithPaginationSupport {
    private MiniAppCommandCrud databaseCrud;
    private UserCrud userCrud;
    private SuperAppObjectCrud superAppObjectCrud;
    private String springApplicationName;
    private ObjectMapper jackson;
    private JmsTemplate jmsTemplate;
    private MiniAppService miniAppCommandService;
    private ApplicationContext applicationContext;
    private MiniAppCommandConverter miniAppCommandConverter;
    private ObjectConverter objectConverter;
    private Log logger = LogFactory.getLog(MiniAppCommandsServiceMongoDb.class);


    /**
     * this method injects a configuration value of spring
     */
    @Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
    public void setSpringApplicationName(String springApplicationName) {
        logger.trace("Entering into setSpringApplicationName with the param: " + springApplicationName);
        this.springApplicationName = springApplicationName;
    }

    @Autowired
    public MiniAppCommandsServiceMongoDb(MiniAppCommandCrud miniAppCommandCrud, UserCrud userCrud, ApplicationContext applicationContext,
                                         MiniAppCommandConverter miniAppCommandConverter, ObjectConverter objectConverter
            , SuperAppObjectCrud superAppObjectCrud) {
        logger.trace("Entering into MiniAppCommandsServiceMongoDb constructor");
        this.databaseCrud = miniAppCommandCrud;
        this.userCrud = userCrud;
        this.applicationContext = applicationContext;
        this.miniAppCommandConverter = miniAppCommandConverter;
        this.objectConverter = objectConverter;
        this.superAppObjectCrud = superAppObjectCrud;
    }

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        logger.trace("Entering into setJmsTemplate");
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setDeliveryDelay(3000L);
    }

    /**
     * this method is invoked after values are injected to instance
     */
    @PostConstruct
    public void init() {
        System.err.println("******** " + this.springApplicationName);
        logger.trace("Entering into init");
        this.jackson = new ObjectMapper();
    }

    /**
     * Activate a given command from a miniApp
     *
     * @param command *
     * @return Object
     */
    @Override
    @Deprecated
    public Object invokeCommand(MiniAppCommandBoundary command) {
        logger.warn("Deprecated method invokeCommand");
        throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");
    }

    /**
     * Activate a given command from a miniApp
     *
     * @param command   command object
     * @param asyncFlag Asynchronized flag
     * @return Object
     */
    @Override
    public Object invokeCommand(MiniAppCommandBoundary command, String miniAppName, Boolean asyncFlag) {
        logger.trace("Entering into invokeCommand with the params: " + command + " " + miniAppName + " " + asyncFlag);
        if (command == null) {
            throw new MiniAppCommandNotFoundException("The command is null");
        }
        CommandId newCommandId = new CommandId();
        newCommandId.setMiniapp(miniAppName);
        newCommandId.setSuperapp(springApplicationName);
        command.setCommandId(newCommandId);
        UUID uuid = UUID.randomUUID();
        command.setInvocationTimestamp(new Date());
        command.getCommandId().setInternalCommandId(uuid.toString());
        logger.trace("After setting the commandId: " + command);
        checkValidCommand(command);
        MiniAppCommandEntity miniAppCommandEntity = miniAppCommandConverter.boundaryToEntity(command);
        miniAppCommandEntity.setCommandId(command.getCommandId().getSuperapp() + Constants.DELIMITER
                + command.getCommandId().getMiniapp() + Constants.DELIMITER + command.getCommandId().getInternalCommandId());
        logger.trace("After setting the commandId to the entity if that id: " + miniAppCommandEntity);
        if (asyncFlag) {
            logger.trace("The command is async");
            return aSyncHandleCommand(command);
        }
        Object commandResult = this.handleCommand(command);
        this.databaseCrud.save(miniAppCommandEntity);//saving the command
        logger.trace("Exiting from invokeCommand with the result: " + commandResult);
        return commandResult;
    }

    private void checkValidCommand(MiniAppCommandBoundary command) {
        logger.trace("Entering into checkValidCommand with the param: " + command);
        this.checkCommand(command);
        String userId = command.getInvokedBy().getUserId().getSuperapp() + Constants.DELIMITER + command.getInvokedBy().getUserId().getEmail();
        UserEntity userEntity = this.userCrud.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        logger.trace("After getting the userEntity: " + userEntity);
        if (userEntity.getRole() != UserRole.MINIAPP_USER) {
            logger.warn("The user is not allowed to invoke commands: " + userEntity);
            throw new UnauthorizedAccessException("The user is not allowed");
        }
        logger.trace("Exiting from checkValidCommand");
    }

    private void checkCommand(MiniAppCommandBoundary command) {
        logger.trace("Entering into checkCommand with the param: " + command);
        if (command.getCommandId() == null) {
            logger.warn("The command's ID is null " + command);
            throw new MiniAppCommandNotFoundException("The command's ID is null");
        }
        if (command.getCommandId().getInternalCommandId() == null) {
            logger.warn("The command's internal ID is null " + command);
            throw new MiniAppCommandNotFoundException("The command's internal ID is empty");
        }
        if (command.getCommand() == null || command.getCommand().trim().isEmpty()) {
            logger.warn("The command string is empty " + command);
            throw new MiniAppCommandNotFoundException("Command string is empty");
        }
        if (command.getTargetObject() == null) {
            logger.warn("The command's target object is null " + command);
            throw new MiniAppCommandNotFoundException("The command's target object is null");
        }
        TargetObject targetObject = command.getTargetObject();
        if (targetObject.getObjectId() == null) {
            logger.warn("The command's target object ID is null " + command);
            throw new MiniAppCommandNotFoundException("The command's target object ID is null");
        }
        if (!this.objectConverter.isActiveObject(this.objectConverter.objectIdToString(command.getTargetObject().getObjectId()), this.superAppObjectCrud)) {
            logger.warn("The object is not in the Database " + command);
            throw new SuperAppObjectNotActiveException("The object is not in the Database");
        }
        ObjectId targetObjectId = targetObject.getObjectId();
        if (targetObjectId.getInternalObjectId().isBlank() || targetObjectId.getSuperapp().isBlank()) {
            logger.warn("The command's target object internal ID is empty " + command);
            throw new MiniAppCommandNotFoundException("The command's target object internal ID is empty");
        }
        if (command.getInvokedBy() == null) {
            logger.warn("The command's InvokedBy is null " + command);
            throw new MiniAppCommandNotFoundException("The command's InvokedBy is null");
        }
        if (command.getInvokedBy().getUserId() == null) {
            logger.warn("The command's user ID is null " + command);
            throw new MiniAppCommandNotFoundException("The command's user ID is null");
        }
        UserId userId = command.getInvokedBy().getUserId();
        if (userId.getEmail().isBlank()) {
            logger.warn("The command's user email is empty " + command);
            throw new MiniAppCommandNotFoundException("The command's user email is empty");
        }
        if (userId.getSuperapp().isBlank()) {
            logger.warn("The command's superapp is empty " + command);
            throw new MiniAppCommandNotFoundException("The command's superapp is empty");
        }
        logger.trace("Exiting from checkCommand with the param: " + command);
    }

    @Override
    public MiniAppCommandBoundary aSyncHandleCommand(MiniAppCommandBoundary command) {
        logger.trace("Entering into aSyncHandleCommand with the param: " + command);
        String json;
        try {
            json = this.jackson.writeValueAsString(command);
            this.jmsTemplate.convertAndSend("commandQueue", json);
            logger.trace("Exiting from aSyncHandleCommand with the param: " + command);
            return command;
        } catch (Exception e) {
            logger.warn("The command was not sent to the queue: " + command);
            throw new RuntimeException();
        }
    }

    @Override
    @JmsListener(destination = "commandQueue")
    public void listenToCommandQueue(String json) {
        try {
            logger.trace("Entering into listenToCommandQueue with the param: " + json);
            MiniAppCommandBoundary command = this.jackson.readValue(json, MiniAppCommandBoundary.class);
            this.handleCommand(command);
            MiniAppCommandEntity miniAppCommandEntity = this.miniAppCommandConverter.boundaryToEntity(command);
            this.databaseCrud.save(miniAppCommandEntity);
        } catch (Exception e) {
            logger.warn("The command was not handled: " + json);
            e.printStackTrace();
        }
    }

    private Object handleCommand(MiniAppCommandBoundary command) {
        logger.trace("Entering into handleCommand with the param: " + command);
        String miniApp = command.getCommandId().getMiniapp();
        switch (miniApp) {
            case "miniAppForum": {
                logger.trace("Entering into miniAppForum with the param: " + command);
                this.miniAppCommandService = this.applicationContext.getBean(miniApp, MiniAppForum.class);
                break;
            }
            case "miniAppGradeAVG": {
                logger.trace("Entering into miniAppGradeAVG with the param: " + command);
                this.miniAppCommandService = this.applicationContext.getBean(miniApp, MiniAppGradeAVG.class);
                break;
            }
            default:
                return command;
        }
        logger.trace("Running the command: " + command);
        return this.miniAppCommandService.runCommand(command);
    }

    /**
     * Get all commands from DB
     *
     * @return List<MiniAppCommandBoundary>
     */
    @Override
    @Deprecated
    public List<MiniAppCommandBoundary> getAllCommands() {
        logger.warn("Deprecated method: getAllCommands");
        throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");

    }


    /**
     * Get all commands from DB
     * @param userSuperapp String
     * @param userEmail String
     * @param size int
     * @param page int
     * @return List<MiniAppCommandBoundary>
     */


    @Override
    public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page) {
        logger.trace("Entering into getAllCommands with the params: " + userSuperapp + " " + userEmail + " " + size + " " + page);
        String userId = userSuperapp + Constants.DELIMITER + userEmail;
        UserEntity user = this.userCrud.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
        logger.trace("Found user in the DB: " + user);
        if (user.getRole() == UserRole.ADMIN) {
            logger.trace("User is admin, returning all commands...");
            return this.databaseCrud
                    .findAll(PageRequest.of(page, size, Direction.ASC, "invocationTimestamp", "commandId")) // List<MiniAppCommandEntity>
                    .stream() // Stream<MiniAppCommandEntity>
                    .map(this.miniAppCommandConverter::entityToBoundary) // Stream<MiniAppCommandEntity>
                    .toList(); // List<MiniAppCommandBoundary>
        } else {
            logger.trace("User is not admin, throwing exception");
            throw new UnauthorizedAccessException("User doesn't have permissions!");
        }

    }


    /**
     * Get all commands from specific miniApp from DB
     * @return List<MiniAppCommandBoundary>
     */
    @Override
    @Deprecated
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
        logger.warn("Deprecated method: getAllMiniAppCommands");
        throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");

    }

    /**
     * Get all commands from specific miniApp from DB
     *
     * @param miniAppName String
     * @param userSuperapp String
     * @param userEmail String
     * @param size int
     * @param page int
     *
     * @return List<MiniAppCommandBoundary>
     */
    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail,
                                                              int size, int page) {
        logger.trace("Entering into getAllMiniAppCommands with the params: " + miniAppName + " " + userSuperapp + " " + userEmail + " " + size + " " + page);
        List<MiniAppCommandBoundary> specificCommands = new ArrayList<>();
        List<MiniAppCommandBoundary> allCommands = getAllCommands(userSuperapp, userEmail, size, page);
        for (MiniAppCommandBoundary cmd : allCommands) {
            if (cmd.getCommandId().getMiniapp().equals(miniAppName)) {
                logger.trace("Found command for " + miniAppName + ": " + cmd);
                specificCommands.add(cmd);
            }
        }
        logger.trace("Returning all commands for " + miniAppName + ": " + specificCommands);
        return specificCommands;
    }


    /**
     * Delete all users from DB
     */
    @Override
    @Deprecated
    public void deleteAllCommands() {
        logger.warn("Deprecated method: deleteAllCommands");
        throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");
    }

    /**
     * Delete all users from DB
     *
     * @param userSuperapp String
     * @param userEmail String
     */
    @Override
    public void deleteAllCommands(String userSuperapp, String userEmail) {
        logger.trace("Entering into deleteAllCommands with the params: " + userSuperapp + " " + userEmail);
        String userId = userSuperapp + Constants.DELIMITER + userEmail;
        UserEntity user = this.userCrud.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
        logger.trace("Found user in the DB: " + user);
        if (user.getRole() == UserRole.ADMIN) {
            logger.trace("User is admin, deleting all commands...");
            this.databaseCrud.deleteAll();
        } else {
            logger.trace("User is not admin, throwing exception");
            throw new UnauthorizedAccessException("User doesn't have permissions!");
        }
    }

}