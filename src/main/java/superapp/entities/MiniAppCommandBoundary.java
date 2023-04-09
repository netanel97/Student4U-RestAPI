package superapp.entities;

import java.util.Date;

public class MiniAppCommandBoundary {

	private CommandId commandId;
	private String command;
	private TargetObject targetObject;
	private Date invocationTimestamp;
	private InvokedBy invokedBy;
	private CommandAttributes commandAttributes;
	
	public MiniAppCommandBoundary() {
		super();
	}
	
	public MiniAppCommandBoundary(CommandId commandId, String command, TargetObject targetObject
			, InvokedBy invokedBy, CommandAttributes commandAttributes) {
		super();
		this.commandId = commandId;
		this.command = command;
		this.targetObject = targetObject;
		invocationTimestamp = new Date();
		this.invokedBy = invokedBy;
		this.commandAttributes = commandAttributes;
	}

	public CommandId getCommandId() {
		return commandId;
	}
	public void setCommandId(CommandId commandId) {
		this.commandId = commandId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public TargetObject getTargetObject() {
		return targetObject;
	}
	public void setTargetObject(TargetObject targetObject) {
		this.targetObject = targetObject;
	}
	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}
	public void setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
	}
	public InvokedBy getInvokedBy() {
		return invokedBy;
	}
	public void setInvokedBy(InvokedBy invokedBy) {
		this.invokedBy = invokedBy;
	}
	public CommandAttributes getCommandAttributes() {
		return commandAttributes;
	}
	public void setCommandAttributes(CommandAttributes commandAttributes) {
		this.commandAttributes = commandAttributes;
	}

	@Override
	public String toString() {
		return "MiniAppCommandBoundary [commandId=" + commandId + ", command=" + command + ", targetObject="
				+ targetObject + ", invocationTimestamp=" + invocationTimestamp + ", invokedBy=" + invokedBy
				+ ", commandAttributes=" + commandAttributes + "]";
	}
	
	
	
}


