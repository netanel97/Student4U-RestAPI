//package superapp.logic.mockup;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import jakarta.annotation.PostConstruct;
//import superapp.data.MiniAppCommandEntity;
//import superapp.entities.CommandId;
//import superapp.entities.InvokedBy;
//import superapp.entities.MiniAppCommandBoundary;
//import superapp.entities.ObjectId;
//import superapp.entities.TargetObject;
//import superapp.entities.UserId;
//import superapp.logic.MiniAppCommandsService;
//
//@Service
//public class MiniAppCommandsServiceMockup implements MiniAppCommandsService {
//	private Map<String, MiniAppCommandEntity> databaseMockup;
//	private String springApplicationName;
//	private String DELIMITER = "_";
//
//	/**
//	 * this method injects a configuration value of spring
//	 */
//	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
//	public void setSpringApplicationName(String springApllicationName) {
//		this.springApplicationName = springApllicationName;
//	}
//
//	/**
//	 * this method is invoked after values are injected to instance
//	 */
//	@PostConstruct
//	public void init() {
//		// create a thread safe map
//		this.databaseMockup = Collections.synchronizedMap(new HashMap<>());
//		System.err.println("******** " + this.springApplicationName);
//	}
//
//	/**
//	 * Activate a given command from a miniApp
//	 *
//	 * @param MiniAppCommandBoundary
//	 *
//	 * @param String
//	 *
//	 * @return Object
//	 */
//
//	@Override
//	public Object invokeCommand(MiniAppCommandBoundary command) {
//		if (command == null) {
//			throw new RuntimeException("miniAppCommandBoundary is null");
//		}
//
//		command.getCommandId().setSuperApp(springApplicationName);
//		MiniAppCommandEntity miniAppCommandEntity = this.boundaryToEntity(command);
//		this.databaseMockup.put(
//				command.getCommandId().getMiniApp() + "-" + command.getCommandId().getInternalCommandId(),
//				miniAppCommandEntity);
//		return this.entityToBoundary(miniAppCommandEntity);
//	}
//
//	/**
//	 * Get all commands from DB
//	 *
//	 * @return List<MiniAppCommandBoundary>
//	 */
//	@Override
//	public List<MiniAppCommandBoundary> getAllCommands() {
//		return this.databaseMockup.values().stream().map(this::entityToBoundary).toList();
//	}
//
//	/**
//	 * Get all commands from specific miniApp from DB
//	 *
//	 * @param String
//	 *
//	 * @return List<MiniAppCommandBoundary>
//	 */
//	@Override
//	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
//		List<MiniAppCommandBoundary> specificCommands = new ArrayList<>();
//		for (String miniAppFromDb : databaseMockup.keySet()) {
//			if (miniAppFromDb.contains(miniAppName)) {
//				specificCommands.add(this.entityToBoundary(databaseMockup.get(miniAppFromDb)));
//			}
//		}
//		return specificCommands;
//	}
//
//	/**
//	 * Convert miniapp command boundary to miniapp command entity
//	 *
//	 * @param MiniAppCommandBoundary
//	 * @return MiniAppCommandEntity
//	 */
//	private MiniAppCommandEntity boundaryToEntity(MiniAppCommandBoundary miniAppCommandBoundary) {
//		MiniAppCommandEntity miniAppCommandEntity = new MiniAppCommandEntity();
//		miniAppCommandEntity.setCommand(miniAppCommandBoundary.getCommand());
//		miniAppCommandEntity.setCommandAttributes(miniAppCommandBoundary.getCommandAttributes());
//		miniAppCommandEntity.setCommandId(this.toEntityCommandId(miniAppCommandBoundary.getCommandId())); // commandID
//																											// cannot be
//																											// null
//		if (miniAppCommandBoundary.getInvokedBy() != null) {
//			miniAppCommandEntity.setInvokedBy(this.toEntityInvokedBy(miniAppCommandBoundary.getInvokedBy()));
//		} else {
//			miniAppCommandEntity.setInvokedBy(this.toEntityInvokedBy(new InvokedBy(new UserId("default"))));
//		}
//		if (miniAppCommandBoundary.getTargetObject() != null) {
//			miniAppCommandEntity.setTargetObject(this.toEntityTargetObject(miniAppCommandBoundary.getTargetObject()));
//		} else {
//			miniAppCommandEntity.setTargetObject(this.toEntityTargetObject(new TargetObject(new ObjectId("default"))));
//		}
//		miniAppCommandEntity.setInvocationTimestamp(miniAppCommandBoundary.getInvocationTimestamp());
//		return miniAppCommandEntity;
//	}
//
//	/**
//	 * Convert TargetObject to String for entity
//	 *
//	 * @param TargetObject
//	 * @return String
//	 */
//	private String toEntityTargetObject(TargetObject targetObject) {
//		return springApplicationName + DELIMITER + targetObject.getObjectId().getInternalObjectId();
//	}
//
//	/**
//	 * Convert InvokedBy object to String for entity
//	 *
//	 * @param InvokedBy
//	 * @return String
//	 */
//	private String toEntityInvokedBy(InvokedBy invokedBy) {
//		return springApplicationName + DELIMITER + invokedBy.getUserId().getEmail();
//	}
//
//	/**
//	 * Convert CommandId object to String for entity
//	 *
//	 * @param CommandId
//	 * @return String
//	 */
//	private String toEntityCommandId(CommandId commandId) {
//		return springApplicationName + DELIMITER + commandId.getMiniApp() + DELIMITER
//				+ commandId.getInternalCommandId();
//	}
//
//	/**
//	 * Convert miniapp command entity to miniapp command boundary
//	 *
//	 * @param MiniAppCommandEntity
//	 * @return MiniAppCommandBoundary
//	 */
//	private MiniAppCommandBoundary entityToBoundary(MiniAppCommandEntity miniAppCommandEntity) {
//		MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary();
//		miniAppCommandBoundary.setCommand(miniAppCommandEntity.getCommand());
//		miniAppCommandBoundary.setCommandAttributes(miniAppCommandEntity.getCommandAttributes());
//		miniAppCommandBoundary.setCommandId(this.toBoundaryCommandId(miniAppCommandEntity.getCommandId()));
//		miniAppCommandBoundary.setInvokedBy(this.toBoundaryInvokedBy(miniAppCommandEntity.getInvokedBy()));
//		miniAppCommandBoundary.setTargetObject(this.toBoundaryTargetObject(miniAppCommandEntity.getTargetObject()));
//		miniAppCommandBoundary.setInvocationTimestamp(miniAppCommandEntity.getInvocationTimestamp());
//		return miniAppCommandBoundary;
//	}
//
//	/**
//	 * Convert String to CommandId object for boundary
//	 *
//	 * @param String
//	 * @return CommandId
//	 */
//	private CommandId toBoundaryCommandId(String commandId) {
//		if (commandId != null) {
//			CommandId newCommandId = new CommandId();
//			String[] attr = commandId.split(DELIMITER);
//			newCommandId.setSuperApp(attr[0]);
//			newCommandId.setMiniApp(attr[1]);
//			newCommandId.setInternalCommandId(attr[2]);
//			return newCommandId;
//		} else
//			return null;
//	}
//
//	/**
//	 * Convert String to TargetObject object for boundary
//	 *
//	 * @param String
//	 * @return TargetObject
//	 */
//	private TargetObject toBoundaryTargetObject(String targetObject) {
//		if (targetObject != null) {
//			String[] attr = targetObject.split(DELIMITER);
//			ObjectId newObjectId = new ObjectId();
//			newObjectId.setSuperApp(attr[0]);
//			newObjectId.setInternalObjectId(attr[1]);
//			TargetObject newTargetObject = new TargetObject();
//			newTargetObject.setObjectId(newObjectId);
//			return newTargetObject;
//		} else
//			return null;
//
//	}
//
//	/**
//	 * Convert String to InvokedBy object for boundary
//	 *
//	 * @param String
//	 * @return InvokedBy
//	 */
//	private InvokedBy toBoundaryInvokedBy(String invokedBy) {
//		if (invokedBy != null) {
//			String[] attr = invokedBy.split(DELIMITER);
//			UserId newUserId = new UserId();
//			newUserId.setSuperApp(attr[0]);
//			newUserId.setEmail(attr[1]);
//			InvokedBy newInvokedBy = new InvokedBy();
//			newInvokedBy.setUserId(newUserId);
//
//			return newInvokedBy;
//		} else
//			return null;
//	}
//
//	/**
//	 * Delete all users from DB
//	 */
//	@Override
//	public void deleteAllCommands() {
//		this.databaseMockup.clear();
//
//	}
//
//}
