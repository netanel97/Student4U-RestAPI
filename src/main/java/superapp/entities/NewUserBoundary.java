package superapp.entities;

public class NewUserBoundary {

	private String role;
	private String username;
	private String avatar;
	private String email;

	public NewUserBoundary() {
		super();
	}

	public NewUserBoundary(String email, String role, String userName, String avatarUrl) {
		super();
		this.email = email;
		this.role = role;
		this.username = userName;
		this.avatar = avatarUrl;
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "UserBoundary [email=" + email + ", role=" + role + ", userName=" + username + ", avatarUrl=" + avatar
				+ "]";
	}

}
