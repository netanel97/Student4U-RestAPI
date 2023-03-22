package entities;

public class UserID {
	
	private String superApp;
	private String email;
	
	public UserID() {
		super();
	}
		
	public UserID(String superApp, String email) {
		super();
		this.superApp = superApp;
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
