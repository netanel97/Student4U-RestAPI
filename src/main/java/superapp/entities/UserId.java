package superapp.entities;

public class UserId {

	private String springApplicationName;
	private String email;

	public UserId() {
		super();
	}

	public UserId(String email) {
		super();
		this.email = email;
	}

	public String getSuperApp() {
		return springApplicationName;
	}

	public String getEmail() {
		return email;
	}

	public void setSuperApp(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserID [springApplicationName=" + springApplicationName + ", email=" + email + "]";
	}
}
