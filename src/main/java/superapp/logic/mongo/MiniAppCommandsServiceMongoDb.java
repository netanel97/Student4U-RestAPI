package superapp.logic.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.data.MiniAppCommandEntity;
import superapp.entities.CommandId;
import superapp.entities.InvokedBy;
import superapp.entities.MiniAppCommandBoundary;
import superapp.entities.MiniAppCommandCrud;
import superapp.entities.ObjectId;
import superapp.entities.TargetObject;
import superapp.entities.UserId;
import superapp.logic.MiniAppCommandNotFoundException;
import superapp.logic.MiniAppCommandsService;

@Service
public class MiniAppCommandsServiceMongoDb implements MiniAppCommandsService {
    private MiniAppCommandCrud databaseCrud;
    private String superapp;
    private String DELIMITER = "_";

    /**
     * this method injects a configuration value of spring
     */
    @Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
    public void setSpringApplicationName(String springApplicationName) {
        this.superapp = springApplicationName;
    }

    @Autowired
    public MiniAppCommandsServiceMongoDb(MiniAppCommandCrud miniAppCommandCrud) {
        this.databaseCrud = miniAppCommandCrud;
    }

    /**
     * this method is invoked after values are injected to instance
     */
    @PostConstruct
    public void init() {
        System.err.println("******** " + this.superapp);
    }

    /**
     * Activate a given command from a miniApp
     *
     * @param MiniAppCommandBoundary
     *
     * @param String
     *
     * @return Object
     */

    @Override
    public Object invokeCommand(MiniAppCommandBoundary command) {
    	if (command == null) {
    		throw new MiniAppCommandNotFoundException("The command is null");
    	}
    	
    	UUID uuid = UUID.randomUUID();
    	command.setInvocationTimestamp(new Date());
    	command.getCommandId().setInternalCommandId(uuid.toString());
    	
    	if (command.getCommandId() == null) {
            throw new MiniAppCommandNotFoundException("The command's ID is null");
        }
    	else if (command.getCommand() == null || command.getCommand().trim().isEmpty())
        {
            throw new MiniAppCommandNotFoundException("Command string is empty");
        }
        else if (command.getCommandId().getInternalCommandId() == null)
        {
        	throw new MiniAppCommandNotFoundException("The command's internal ID is empty");
        }
    	
                
        MiniAppCommandEntity miniAppCommandEntity = this.boundaryToEntity(command);
        miniAppCommandEntity.setCommandId(superapp + DELIMITER + command.getCommandId().getMiniapp() + DELIMITER + command.getCommandId().getInternalCommandId());

        this.databaseCrud.save(miniAppCommandEntity);
        return this.entityToBoundary(miniAppCommandEntity);
    }

    /**
     * Get all commands from DB
     *
     * @return List<MiniAppCommandBoundary>
     */
    @Override
    public List<MiniAppCommandBoundary> getAllCommands() {
        return this.databaseCrud.findAll().stream().map(this::entityToBoundary).toList();
    }

    /**
     * Get all commands from specific miniApp from DB
     *
     * @param String
     *
     * @return List<MiniAppCommandBoundary>
     */
    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
        List<MiniAppCommandBoundary> specificCommands = new ArrayList<>();
        List<MiniAppCommandBoundary> allCommands = getAllCommands();
        for (MiniAppCommandBoundary cmd : allCommands)
        {
            if (cmd.getCommandId().getMiniapp().equals(miniAppName))
            {
                specificCommands.add(cmd);
            }
        }
        if (specificCommands.size() == 0)
        {
            throw new MiniAppCommandNotFoundException("either " + miniAppName + " is non-existent or no commands for " + miniAppName + " were found.");
        }
        return specificCommands;
    }

    /**
     * Convert miniapp command boundary to miniapp command entity
     *
     * @param MiniAppCommandBoundary
     * @return MiniAppCommandEntity
     */
    private MiniAppCommandEntity boundaryToEntity(MiniAppCommandBoundary miniAppCommandBoundary) {
        MiniAppCommandEntity miniAppCommandEntity = new MiniAppCommandEntity();
        miniAppCommandEntity.setCommand(miniAppCommandBoundary.getCommand());
        miniAppCommandEntity.setCommandAttributes(miniAppCommandBoundary.getCommandAttributes());
        miniAppCommandEntity.setCommandId(this.toEntityCommandId(miniAppCommandBoundary.getCommandId())); // commandID
        // cannot be
        // null
        if (miniAppCommandBoundary.getInvokedBy() != null) {
            miniAppCommandEntity.setInvokedBy(this.toEntityInvokedBy(miniAppCommandBoundary.getInvokedBy()));
        } else {
            miniAppCommandEntity.setInvokedBy(this.toEntityInvokedBy(new InvokedBy(new UserId("default"))));
        }
        if (miniAppCommandBoundary.getTargetObject() != null) {
            miniAppCommandEntity.setTargetObject(this.toEntityTargetObject(miniAppCommandBoundary.getTargetObject()));
        } else {
            miniAppCommandEntity.setTargetObject(this.toEntityTargetObject(new TargetObject(new ObjectId("default"))));
        }
        miniAppCommandEntity.setInvocationTimestamp(miniAppCommandBoundary.getInvocationTimestamp());
        return miniAppCommandEntity;
    }

    /**
     * Convert TargetObject to String for entity
     *
     * @param TargetObject
     * @return String
     */
    private String toEntityTargetObject(TargetObject targetObject) {
        return superapp + DELIMITER + targetObject.getObjectId().getInternalObjectId();
    }

    /**
     * Convert InvokedBy object to String for entity
     *
     * @param InvokedBy
     * @return String
     */
    private String toEntityInvokedBy(InvokedBy invokedBy) {
        return superapp + DELIMITER + invokedBy.getUserId().getEmail();
    }

    /**
     * Convert CommandId object to String for entity
     *
     * @param CommandId
     * @return String
     */
    private String toEntityCommandId(CommandId commandId) {
        return superapp + DELIMITER + commandId.getMiniapp() + DELIMITER
                + commandId.getInternalCommandId();
    }

    /**
     * Convert miniapp command entity to miniapp command boundary
     *
     * @param MiniAppCommandEntity
     * @return MiniAppCommandBoundary
     */
    private MiniAppCommandBoundary entityToBoundary(MiniAppCommandEntity miniAppCommandEntity) {
        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary();
        miniAppCommandBoundary.setCommand(miniAppCommandEntity.getCommand());
        miniAppCommandBoundary.setCommandAttributes(miniAppCommandEntity.getCommandAttributes());
        miniAppCommandBoundary.setCommandId(this.toBoundaryCommandId(miniAppCommandEntity.getCommandId()));
        miniAppCommandBoundary.setInvokedBy(this.toBoundaryInvokedBy(miniAppCommandEntity.getInvokedBy()));
        miniAppCommandBoundary.setTargetObject(this.toBoundaryTargetObject(miniAppCommandEntity.getTargetObject()));
        miniAppCommandBoundary.setInvocationTimestamp(miniAppCommandEntity.getInvocationTimestamp());
        return miniAppCommandBoundary;
    }

    /**
     * Convert String to CommandId object for boundary
     *
     * @param String
     * @return CommandId
     */
    private CommandId toBoundaryCommandId(String commandId) {
        if (commandId != null) {
            CommandId newCommandId = new CommandId();
            String[] attr = commandId.split(DELIMITER);
            newCommandId.setSuperapp(commandId);
            newCommandId.setMiniApp(attr[1]);
            newCommandId.setInternalCommandId(attr[2]);
            return newCommandId;
        } else
            return null;
    }

    /**
     * Convert String to TargetObject object for boundary
     *
     * @param String
     * @return TargetObject
     */
    private TargetObject toBoundaryTargetObject(String targetObject) {
        if (targetObject != null) {
            String[] attr = targetObject.split(DELIMITER);
            ObjectId newObjectId = new ObjectId();
            newObjectId.setSuperApp(attr[0]);
            newObjectId.setInternalObjectId(attr[1]);
            TargetObject newTargetObject = new TargetObject();
            newTargetObject.setObjectId(newObjectId);
            return newTargetObject;
        } else
            return null;

    }

    /**
     * Convert String to InvokedBy object for boundary
     *
     * @param String
     * @return InvokedBy
     */
    private InvokedBy toBoundaryInvokedBy(String invokedBy) {
        if (invokedBy != null) {
            String[] attr = invokedBy.split(DELIMITER);
            UserId newUserId = new UserId();
            newUserId.setSuperApp(attr[0]);
            newUserId.setEmail(attr[1]);
            InvokedBy newInvokedBy = new InvokedBy();
            newInvokedBy.setUserId(newUserId);

            return newInvokedBy;
        } else
            return null;
    }

    /**
     * Delete all users from DB
     */
    @Override
    public void deleteAllCommands() {
        this.databaseCrud.deleteAll();

    }

}