package superapp.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import superapp.boundaries.command.CommandId;
import superapp.boundaries.command.InvokedBy;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.TargetObject;
import superapp.boundaries.object.ObjectId;
import superapp.boundaries.user.UserId;
import superapp.data.MiniAppCommandEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class MiniAppCommandConverter {

	private String springApplicationName;
	private Log logger = LogFactory.getLog(MiniAppCommandConverter.class);


	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	/**
	 * Convert miniapp command entity to miniapp command boundary
	 *
	 * @param miniAppCommandEntity
	 * @return MiniAppCommandBoundary
	 */
	public MiniAppCommandBoundary entityToBoundary(MiniAppCommandEntity miniAppCommandEntity) {
		logger.trace("Entering to entityToBoundary method with the following parameters: " + miniAppCommandEntity);
		MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary();
		miniAppCommandBoundary.setCommand(miniAppCommandEntity.getCommand());
		miniAppCommandBoundary.setCommandAttributes(miniAppCommandEntity.getCommandAttributes());
		miniAppCommandBoundary.setCommandId(this.toBoundaryCommandId(miniAppCommandEntity.getCommandId()));
		miniAppCommandBoundary.setInvokedBy(this.toBoundaryInvokedBy(miniAppCommandEntity.getInvokedBy()));
		miniAppCommandBoundary.setTargetObject(this.toBoundaryTargetObject(miniAppCommandEntity.getTargetObject()));
		miniAppCommandBoundary.setInvocationTimestamp(miniAppCommandEntity.getInvocationTimestamp());
		logger.trace("Exiting from entityToBoundary method with the following parameters: " + miniAppCommandBoundary);
		return miniAppCommandBoundary;
	}
	/**
	 *
	 miniAppCommandEntity.setCommandId(command.getCommandId().getSuperapp() + Constants.DELIMITER
	 + command.getCommandId().getMiniapp() + Constants.DELIMITER + command.getCommandId().getInternalCommandId());
	 */

	/**
	 * Convert miniapp command boundary to miniapp command entity
	 *
	 * @param miniAppCommandBoundary
	 * @return MiniAppCommandEntity
	 */
	public MiniAppCommandEntity boundaryToEntity(MiniAppCommandBoundary miniAppCommandBoundary) {
		logger.trace("Entering to boundaryToEntity method with the following parameters: " + miniAppCommandBoundary);
		MiniAppCommandEntity miniAppCommandEntity = new MiniAppCommandEntity();
		miniAppCommandEntity.setCommand(miniAppCommandBoundary.getCommand());
		logger.debug("command: " + miniAppCommandBoundary.getCommand());
		miniAppCommandEntity.setCommandAttributes(miniAppCommandBoundary.getCommandAttributes());
		miniAppCommandEntity.setCommandId(this.toEntityCommandId(miniAppCommandBoundary.getCommandId())); // commandID
		logger.debug("commandId: " + miniAppCommandBoundary.getCommandId());
		if (miniAppCommandBoundary.getInvokedBy() != null) {
			logger.trace("Passed invokedBy check");
			miniAppCommandEntity.setInvokedBy(this.toEntityInvokedBy(miniAppCommandBoundary.getInvokedBy()));
		} else {
			logger.trace("Failed invokedBy check");
			miniAppCommandEntity.setInvokedBy(this.toEntityInvokedBy(new InvokedBy(new UserId("default"))));
		}
		if (miniAppCommandBoundary.getTargetObject() != null) {
			logger.trace("Passed targetObject check");
			miniAppCommandEntity.setTargetObject(this.toEntityTargetObject(miniAppCommandBoundary.getTargetObject()));
		} else {
			logger.trace("Failed targetObject check");
			miniAppCommandEntity.setTargetObject(this.toEntityTargetObject(new TargetObject(new ObjectId("default"))));
		}
		miniAppCommandEntity.setInvocationTimestamp(miniAppCommandBoundary.getInvocationTimestamp());
		logger.trace("Exiting from boundaryToEntity method with the following parameters: " + miniAppCommandEntity);
		return miniAppCommandEntity;
	}

	/**
	 * Convert String to CommandId object for boundary
	 *
	 * @param  commandId
	 * @return CommandId
	 */
	public CommandId toBoundaryCommandId(String commandId) {
		logger.trace("Entering to toBoundaryCommandId method with the following parameters: " + commandId);
		if (commandId != null) {
			CommandId newCommandId = new CommandId();
			String[] attr = commandId.split(Constants.DELIMITER);
			newCommandId.setSuperapp(attr[0]);
			newCommandId.setMiniapp(attr[1]);
			newCommandId.setInternalCommandId(attr[2]);
			logger.trace("Exiting from toBoundaryCommandId method with the following parameters: " + newCommandId);
			return newCommandId;
		} else
			return null;
	}


	/**
	 * Convert String to InvokedBy object for boundary
	 *
	 * @param invokedBy
	 * @return InvokedBy
	 */
	private InvokedBy toBoundaryInvokedBy(String invokedBy) {
		logger.trace("Entering to toBoundaryInvokedBy method with the following parameters: " + invokedBy);
		if (invokedBy != null) {
			String[] attr = invokedBy.split(Constants.DELIMITER);
			UserId newUserId = new UserId();
			newUserId.setSuperapp(attr[0]);
			newUserId.setEmail(attr[1]);
			InvokedBy newInvokedBy = new InvokedBy();
			newInvokedBy.setUserId(newUserId);
			logger.trace("Exiting from toBoundaryInvokedBy method with the following parameters: " + newInvokedBy);
			return newInvokedBy;
		} else
			return null;
	}

	/**
	 * Convert String to TargetObject object for boundary
	 *
	 * @param targetObject
	 * @return TargetObject
	 */
	private TargetObject toBoundaryTargetObject(String targetObject) {
		logger.trace("Entering to toBoundaryTargetObject method with the following parameters: " + targetObject);
		if (targetObject != null) {
			String[] attr = targetObject.split(Constants.DELIMITER);
			ObjectId newObjectId = new ObjectId();
			newObjectId.setSuperapp(attr[0]);
			newObjectId.setInternalObjectId(attr[1]);
			TargetObject newTargetObject = new TargetObject();
			newTargetObject.setObjectId(newObjectId);
			logger.trace("Exiting from toBoundaryTargetObject method with the following parameters: " + newTargetObject);
			return newTargetObject;
		} else
			return null;
	}

	/**
	 * Convert TargetObject to String for entity
	 *
	 * @param targetObject
	 * @return String
	 */
	private String toEntityTargetObject(TargetObject targetObject) {
		logger.trace("Entering to toEntityTargetObject method with the following parameters: " + targetObject);
		logger.trace("Exiting from toEntityTargetObject method with the following parameters: " + springApplicationName + Constants.DELIMITER + targetObject.getObjectId().getInternalObjectId());
		return springApplicationName + Constants.DELIMITER + targetObject.getObjectId().getInternalObjectId();
	}

	/**
	 * Convert InvokedBy object to String for entity
	 *
	 * @param invokedBy
	 * @return String
	 */
	private String toEntityInvokedBy(InvokedBy invokedBy) {
		logger.trace("Entering to toEntityInvokedBy method with the following parameters: " + invokedBy);
		logger.trace("Exiting from toEntityInvokedBy method with the following parameters: " + springApplicationName + Constants.DELIMITER + invokedBy.getUserId().getEmail());
		return springApplicationName + Constants.DELIMITER + invokedBy.getUserId().getEmail();
	}

	/**
	 * Convert CommandId object to String for entity
	 *
	 * @param commandId
	 * @return String
	 */
	private String toEntityCommandId(CommandId commandId) {
		logger.trace("Entering to toEntityCommandId method with the following parameters: " + commandId);
		logger.trace("Exiting from toEntityCommandId method with the following parameters: " + springApplicationName + Constants.DELIMITER + commandId.getMiniapp() + Constants.DELIMITER + commandId.getInternalCommandId());
		return springApplicationName + Constants.DELIMITER + commandId.getMiniapp() + Constants.DELIMITER + commandId.getInternalCommandId();
	}


	public Date stringToDate(String dateString){
		logger.trace("Entering to stringToDate method with the following parameters: " + dateString);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			logger.warn("Date format is incorrect.");
			throw new DateFormatIncorrectException("Date format is incorrect.");
		}
	}

	public int getPage(Map<String, Object> commandAtt) {
		logger.trace("Entering to getPage method with the following parameters: " + commandAtt);
		if (commandAtt.containsKey("page")) {
			return (int) commandAtt.get("page");
		} else{
			logger.trace("Exiting from getPage method with the following parameters: " + Constants.DEFAULT_PAGE_VALUE_INT);
			return Constants.DEFAULT_PAGE_VALUE_INT;
		}
	}

	public int getSize(Map<String, Object> commandAtt) {
		logger.trace("Entering to getSize method with the following parameters: " + commandAtt);
		if (commandAtt.containsKey("size")) {
			return (int) commandAtt.get("size");
		} else{
			logger.trace("Exiting from getSize method with the following parameters: " + Constants.DEFAULT_SIZE_VALUE_INT);
			return Constants.DEFAULT_SIZE_VALUE_INT;
		}
	}
}
