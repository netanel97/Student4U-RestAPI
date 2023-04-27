package superapp.entities;

public class UserId {

	private String superapp;
	private String email;

	public UserId() {
		super();
	}

	public UserId(String email) {
		super();
		this.email = email;
	}

	public String getSuperApp() {
		return superapp;
	}

	public String getEmail() {
		return email;
	}

	public void setSuperApp(String springApplicationName) {
		this.superapp = springApplicationName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserID [springApplicationName=" + superapp + ", email=" + email + "]";
	}
}
