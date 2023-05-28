package superapp.utils;

import org.springframework.stereotype.Component;

import superapp.boundaries.command.CommandId;
import superapp.boundaries.command.InvokedBy;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.TargetObject;
import superapp.boundaries.object.ObjectId;
import superapp.boundaries.user.UserId;
import superapp.data.MiniAppCommandEntity;

@Component
public class MiniAppCommandConverter {
	
	private String DELIMITER = "_";
	
	/**
	 * Convert miniapp command entity to miniapp command boundary
	 *
	 * @param MiniAppCommandEntity
	 * @return MiniAppCommandBoundary
	 */
	public MiniAppCommandBoundary entityToBoundary(MiniAppCommandEntity miniAppCommandEntity) {
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
	public CommandId toBoundaryCommandId(String commandId) {
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
			newObjectId.setInternalObjectId(attr[1]);
			TargetObject newTargetObject = new TargetObject();
			newTargetObject.setObjectId(newObjectId);
			return newTargetObject;
		} else
			return null;

	}

}
