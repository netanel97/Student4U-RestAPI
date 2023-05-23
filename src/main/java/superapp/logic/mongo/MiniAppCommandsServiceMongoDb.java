package superapp.logic.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import superapp.data.MiniAppCommandEntity;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.entities.CommandId;
import superapp.entities.InvokedBy;
import superapp.entities.MiniAppCommandBoundary;
import superapp.entities.MiniAppCommandCrud;
import superapp.entities.ObjectId;
import superapp.entities.TargetObject;
import superapp.entities.UserCrud;
import superapp.entities.UserId;
import superapp.logic.MiniAppCommandNotFoundException;
import superapp.logic.MiniAppCommandsServiceWithPaginationSupport;
import superapp.logic.UnauthorizedAccessException;
import superapp.logic.UserNotFoundException;

@Service
public class MiniAppCommandsServiceMongoDb implements MiniAppCommandsServiceWithPaginationSupport {
	private MiniAppCommandCrud databaseCrud;
	private UserCrud userCrud;
	private String superapp;
	private String DELIMITER = "_";
	private ObjectMapper jackson;
	private JmsTemplate jmsTemplate;

	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApplicationName) {
		this.superapp = springApplicationName;
	}

	@Autowired
	public MiniAppCommandsServiceMongoDb(MiniAppCommandCrud miniAppCommandCrud, UserCrud userCrud) {
		this.databaseCrud = miniAppCommandCrud;
		this.userCrud = userCrud;
	}

	@Autowired
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
		this.jmsTemplate.setDeliveryDelay(5000L);
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
		if (command == null) {
			throw new MiniAppCommandNotFoundException("The command is null");
		}

		UUID uuid = UUID.randomUUID();
		command.setInvocationTimestamp(new Date());
		command.getCommandId().setInternalCommandId(uuid.toString());

		if (command.getCommandId() == null) {
			throw new MiniAppCommandNotFoundException("The command's ID is null");
		} else if (command.getCommand() == null || command.getCommand().trim().isEmpty()) {
			throw new MiniAppCommandNotFoundException("Command string is empty");
		} else if (command.getCommandId().getInternalCommandId() == null) {
			throw new MiniAppCommandNotFoundException("The command's internal ID is empty");
		}

		MiniAppCommandEntity miniAppCommandEntity = this.boundaryToEntity(command);
		miniAppCommandEntity.setCommandId(superapp + DELIMITER + command.getCommandId().getMiniapp() + DELIMITER
				+ command.getCommandId().getInternalCommandId());

		this.databaseCrud.save(miniAppCommandEntity);
		return this.entityToBoundary(miniAppCommandEntity);
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

		checkCommand(command);
		

		MiniAppCommandEntity miniAppCommandEntity = this.boundaryToEntity(command);
		miniAppCommandEntity.setCommandId(command.getCommandId().getSuperapp() + DELIMITER
				+ command.getCommandId().getMiniapp() + DELIMITER + command.getCommandId().getInternalCommandId());

		if (asyncFlag) {
			return aSyncHandleCommand(command);
		} else {
			this.databaseCrud.save(miniAppCommandEntity);
			return this.entityToBoundary(miniAppCommandEntity);

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
			MiniAppCommandBoundary miniAppCommandBoundary = this.jackson.readValue(json, MiniAppCommandBoundary.class);

			this.handleCommand(json);
			MiniAppCommandEntity miniAppCommandEntity = this.boundaryToEntity(miniAppCommandBoundary);

			this.databaseCrud.save(miniAppCommandEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleCommand(String json) {
		System.err.println("Doing something...");
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		System.err.println("Did something!");
	}

	/**
	 * Get all commands from DB
	 *
	 * @return List<MiniAppCommandBoundary>
	 */
	@Override
	@Deprecated
	public List<MiniAppCommandBoundary> getAllCommands() {

		return this.databaseCrud.findAll().stream().map(this::entityToBoundary).toList();
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
					.map(this::entityToBoundary) // Stream<MiniAppCommandEntity>
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
		List<MiniAppCommandBoundary> specificCommands = new ArrayList<>();
		List<MiniAppCommandBoundary> allCommands = getAllCommands();
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
		return superapp + DELIMITER + commandId.getMiniapp() + DELIMITER + commandId.getInternalCommandId();
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
			newCommandId.setMiniapp(attr[1]);
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
			newObjectId.setSuperapp(attr[0]);
			System.err.println("attr 0: " + attr[0]);
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
			newUserId.setSuperapp(attr[0]);
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
	@Deprecated
	public void deleteAllCommands() {
		this.databaseCrud.deleteAll();

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