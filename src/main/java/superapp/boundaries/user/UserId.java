package superapp.boundaries.user;

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

	public String getSuperapp() {
		return superapp;
	}

	public String getEmail() {
		return email;
	}

	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserID [superapp=" + superapp + ", email=" + email + "]";
	}
}
