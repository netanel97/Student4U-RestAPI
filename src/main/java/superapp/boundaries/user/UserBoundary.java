package superapp.boundaries.user;

public class UserBoundary {

	private UserId userId;
	private String role;
	private String username;
	private String avatar;

	public UserBoundary() {
		super();
	}

	public UserBoundary(UserId userId, String role, String username, String avatarUrl) {
		super();
		this.userId = userId;
		this.role = role;
		this.username = username;
		this.avatar = avatarUrl;
	}

	public UserId getUserId() {
		return userId;
	}

	public String getRole() {
		return role;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId.toString() + ", role=" + role + ", userName=" + username
				+ ", avatarUrl=" + avatar + "]";
	}

}
