package superapp.entities;

public class CommandId {

	private String springApplicationName;
	private String miniApp;
	private String internalCommandId;
	
	public CommandId() {
		super();
	}
	
	public CommandId(String miniApp, String internalCommandId) {
		super();
		this.miniApp = miniApp;
		this.internalCommandId = internalCommandId;
	}
	
	public String getSuperApp() {
		return springApplicationName;
	}
	
	public void setSuperApp(String springApllicationName) {
		this.springApplicationName = springApllicationName;
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
		return "CommandId [springApplicationName=" + springApplicationName + ", miniApp=" + miniApp + ", internalCommandId=" + internalCommandId
				+ "]";
	}
	
}
