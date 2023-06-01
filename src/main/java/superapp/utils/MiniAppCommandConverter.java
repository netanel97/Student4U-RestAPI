package superapp.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import superapp.boundaries.command.CommandId;
import superapp.boundaries.command.InvokedBy;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.TargetObject;
import superapp.boundaries.object.ObjectId;
import superapp.boundaries.user.UserId;
import superapp.data.MiniAppCommandEntity;
import superapp.logic.mongo.ObjectsServiceMongoDb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MiniAppCommandConverter {
	
	private String DELIMITER = "_";
	private Log logger = LogFactory.getLog(MiniAppCommandConverter.class);

	/**
	 * Convert miniapp command entity to miniapp command boundary
	 *
	 * @param MiniAppCommandEntity
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
	 * Convert String to CommandId object for boundary
	 *
	 * @param String
	 * @return CommandId
	 */
	public CommandId toBoundaryCommandId(String commandId) {
		logger.trace("Entering to toBoundaryCommandId method with the following parameters: " + commandId);
		if (commandId != null) {
			CommandId newCommandId = new CommandId();
			String[] attr = commandId.split(DELIMITER);
			newCommandId.setSuperapp(commandId);
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
	 * @param String
	 * @return InvokedBy
	 */
	private InvokedBy toBoundaryInvokedBy(String invokedBy) {
		logger.trace("Entering to toBoundaryInvokedBy method with the following parameters: " + invokedBy);
		if (invokedBy != null) {
			String[] attr = invokedBy.split(DELIMITER);
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
	 * @param String
	 * @return TargetObject
	 */
	private TargetObject toBoundaryTargetObject(String targetObject) {
		logger.trace("Entering to toBoundaryTargetObject method with the following parameters: " + targetObject);
		if (targetObject != null) {
			String[] attr = targetObject.split(DELIMITER);
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

	public Date stringToDate(String dateString){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Date format is incorrect.");
		}
	}

}
