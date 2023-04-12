package superapp.entities;

import org.springframework.beans.factory.annotation.Value;

public class UserId {
		
	@Value("${spring.application.name:iAmTheDefaultNameOfTheApplication}")
	private String superApp;
	
	private String email;
	
	public UserId() {
		super();
	}
		
	public UserId(String email) {
		super();
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
