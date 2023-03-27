package entities;

public class UserID {
	
	private final String SUPERAPP_NAME = "2023b.LiranSorokin";
	
	private String superApp;
	private String email;
	
	public UserID() {
		super();
	}
		
	public UserID(String email) {
		super();
		this.superApp = SUPERAPP_NAME;
		this.email = email;
	}

	public String getSuperApp() {
		return superApp;
	}
	public String getEmail() {
		return email;
	}
	public void setSuperApp(String superApp) {
		this.superApp = superApp;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserID [superApp=" + superApp + ", email=" + email + "]";
	}
}
