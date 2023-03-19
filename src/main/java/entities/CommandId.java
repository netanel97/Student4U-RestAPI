package entities;

public class CommandId {

	private String superapp;
	private String miniApp;
	private String internalCommandId;
	
	public CommandId() {
		super();
	}
	
	public CommandId(String superapp, String miniApp, String internalCommandId) {
		super();
		this.superapp = superapp;
		this.miniApp = miniApp;
		this.internalCommandId = internalCommandId;
	}
	
	public String getSuperapp() {
		return superapp;
	}
	
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	public String getMiniApp() {
		return miniApp;
	}
	
	public void setMiniApp(String miniApp) {
		this.miniApp = miniApp;
	}
	
	public String getInternalCommandId() {
		return internalCommandId;
	}
	
	public void setInternalCommandId(String internalCommandId) {
		this.internalCommandId = internalCommandId;
	}

	@Override
	public String toString() {
		return "CommandId [superapp=" + superapp + ", miniApp=" + miniApp + ", internalCommandId=" + internalCommandId
				+ "]";
	}
	
	
}
