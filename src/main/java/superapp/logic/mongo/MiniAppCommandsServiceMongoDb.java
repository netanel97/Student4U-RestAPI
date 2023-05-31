package superapp.logic.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import superapp.utils.MiniAppCommandConverter;
import superapp.utils.ObjectConverter;

@Service
public class MiniAppCommandsServiceMongoDb implements MiniAppCommandsServiceWithPaginationSupport {
	private MiniAppCommandCrud databaseCrud;
	private UserCrud userCrud;
	private SuperAppObjectCrud superAppObjectCrud;
	private String superapp;
	private String DELIMITER = "_";
	private ObjectMapper jackson;
	private JmsTemplate jmsTemplate;
	private MiniAppService miniAppCommandService;
	private ApplicationContext applicationContext;
	private MiniAppCommandConverter miniAppCommandConverter;
	private ObjectConverter objectConverter;

	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApplicationName) {
		this.superapp = springApplicationName;
	}

	@Autowired
	public MiniAppCommandsServiceMongoDb(MiniAppCommandCrud miniAppCommandCrud, UserCrud userCrud,ApplicationContext applicationContext,
										 MiniAppCommandConverter miniAppCommandConverter,ObjectConverter objectConverter
										 ,SuperAppObjectCrud superAppObjectCrud) {
		this.databaseCrud = miniAppCommandCrud;
		this.userCrud = userCrud;
		this.applicationContext = applicationContext;
		this.miniAppCommandConverter = miniAppCommandConverter;
		this.objectConverter = objectConverter;
		this.superAppObjectCrud = superAppObjectCrud;
	}

	@Autowired
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
		this.jmsTemplate.setDeliveryDelay(3000L);
	}

	/**
	 * this method is invoked after values are injected to instance
	 */
	@PostConstruct
	public void init() {
		System.err.println("******** " + this.superapp);
		this.jackson = new ObjectMapper();
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
	@Deprecated
	public Object invokeCommand(MiniAppCommandBoundary command) {
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
		if (command == null) {
			throw new MiniAppCommandNotFoundException("The command is null");
		}
		CommandId newCommandId = new CommandId();
		newCommandId.setMiniapp(miniAppName);
		newCommandId.setSuperapp(superapp);
		command.setCommandId(newCommandId);

		UUID uuid = UUID.randomUUID();
		command.setInvocationTimestamp(new Date());
		command.getCommandId().setInternalCommandId(uuid.toString());

		checkValidCommand(command);
		MiniAppCommandEntity miniAppCommandEntity = this.boundaryToEntity(command);
		miniAppCommandEntity.setCommandId(command.getCommandId().getSuperapp() + DELIMITER
				+ command.getCommandId().getMiniapp() + DELIMITER + command.getCommandId().getInternalCommandId());
		if (asyncFlag) {
			return aSyncHandleCommand(command);
		} 
		Object commandResult = this.handleCommand(command);
		System.err.println(commandResult);
		this.databaseCrud.save(miniAppCommandEntity);//saving the command
//		if(commandResult instanceof MiniAppCommandBoundary)
//				return commandResult;
		return commandResult;
	}
	
	private void checkValidCommand(MiniAppCommandBoundary command) {
		this.checkCommand(command);
		String userId = command.getInvokedBy().getUserId().getSuperapp() + DELIMITER + command.getInvokedBy().getUserId().getEmail();
		UserEntity userEntity = this.userCrud.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found"));

		if(userEntity.getRole() != UserRole.MINIAPP_USER) {
			throw new UnauthorizedAccessException("The user is not allowed");
		}
		
	}

	private void checkCommand(MiniAppCommandBoundary command) {
		if (command.getCommandId() == null) {
			throw new MiniAppCommandNotFoundException("The command's ID is null");
		}
		if (command.getCommandId().getInternalCommandId() == null) {
			throw new MiniAppCommandNotFoundException("The command's internal ID is empty");
		}
		if (command.getCommand() == null || command.getCommand().trim().isEmpty()) {
			throw new MiniAppCommandNotFoundException("Command string is empty");
		}
		if (command.getTargetObject() == null) {
			throw new MiniAppCommandNotFoundException("The command's target object is null");
		}
		TargetObject targetObject = command.getTargetObject();
		if (targetObject.getObjectId() == null) {
			throw new MiniAppCommandNotFoundException("The command's target object ID is null");
		}
		if(!this.objectConverter.isActiveObject(this.objectConverter.objectIdToString(command.getTargetObject().getObjectId()), this.superAppObjectCrud))
		{
			throw new SuperAppObjectNotActiveException("The object is not in the Database");
		}
		//TODO:need to change because the prev if
		ObjectId targetObjectId = targetObject.getObjectId();
		if (targetObjectId.getInternalObjectId().isBlank() || targetObjectId.getSuperapp().isBlank()) {
			throw new MiniAppCommandNotFoundException("The command's target object internal ID is empty");
		}
		if(command.getInvokedBy() == null) {
			throw new MiniAppCommandNotFoundException("The command's InvokedBy is null");
		}
		if(command.getInvokedBy().getUserId() == null) {
			throw new MiniAppCommandNotFoundException("The command's user ID is null");
		}
		UserId userId = command.getInvokedBy().getUserId();
		if(userId.getEmail().isBlank()) {
			throw new MiniAppCommandNotFoundException("The command's user email is empty");
		}
		if(userId.getSuperapp().isBlank()) {
			throw new MiniAppCommandNotFoundException("The command's superapp is empty");
		}
	}

	@Override
	public MiniAppCommandBoundary aSyncHandleCommand(MiniAppCommandBoundary command) {
		String json;
		try {
			json = this.jackson.writeValueAsString(command);
			this.jmsTemplate.convertAndSend("commandQueue", json);
			return command;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	@JmsListener(destination = "commandQueue")
	public void listenToCommandQueue(String json) {
		try {
			MiniAppCommandBoundary command = this.jackson.readValue(json, MiniAppCommandBoundary.class);
			
			this.handleCommand(command);
			MiniAppCommandEntity miniAppCommandEntity = this.boundaryToEntity(command);

			this.databaseCrud.save(miniAppCommandEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object handleCommand(MiniAppCommandBoundary command) {
		String miniApp = command.getCommandId().getMiniapp();
		switch (miniApp) {
			case "miniAppForum": {
	        this.miniAppCommandService = this.applicationContext.getBean(miniApp, MiniAppForum.class);
				break;
			}
			case "miniAppGradeAVG":
			{
	            this.miniAppCommandService = this.applicationContext.getBean(miniApp,MiniAppGradeAVG.class);
				break;
			}
			default:
				return command;
		}
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
		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");

	}

	@Override
	public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page) {
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		if (user.getRole() == UserRole.ADMIN) {
			return this.databaseCrud
					.findAll(PageRequest.of(page, size, Direction.ASC, "invocationTimestamp", "commandId")) // List<MiniAppCommandEntity>
					.stream() // Stream<MiniAppCommandEntity>
					.map(this.miniAppCommandConverter::entityToBoundary) // Stream<MiniAppCommandEntity>
					.toList(); // List<MiniAppCommandBoundary>
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}

	}

	/**
	 * Get all commands from specific miniApp from DB
	 *
	 * @param String
	 *
	 * @return List<MiniAppCommandBoundary>
	 */
	@Override
	@Deprecated
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");

	}

	@Override
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail,
															  int size, int page) {
		List<MiniAppCommandBoundary> specificCommands = new ArrayList<>();
		List<MiniAppCommandBoundary> allCommands = getAllCommands(userSuperapp, userEmail, size, page);
		for (MiniAppCommandBoundary cmd : allCommands) {
			if (cmd.getCommandId().getMiniapp().equals(miniAppName)) {
				specificCommands.add(cmd);
			}
		}
		if (specificCommands.size() == 0) {
			throw new MiniAppCommandNotFoundException(
					"either " + miniAppName + " is non-existent or no commands for " + miniAppName + " were found.");
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
		return superapp + DELIMITER + commandId.getMiniapp() + DELIMITER + commandId.getInternalCommandId();
	}


	/**
	 * Delete all users from DB
	 */
	@Override
	@Deprecated
	public void deleteAllCommands() {
		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");


	}

	@Override
	public void deleteAllCommands(String userSuperapp, String userEmail) {
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));

		if (user.getRole() == UserRole.ADMIN) {
			this.databaseCrud.deleteAll();
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

}