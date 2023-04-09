package superapp.data;

public class UserEntity {

	private String userId;
	private String role;
	private String userName;
	private String avatar;

	public UserEntity() {
	}

	public String getUserId() {
		return userId;
	}

	public String getRole() {
		return role;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserId(String userId) {
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

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId.toString() + ", role=" + role + ", userName=" + userName
				+ ", avatarUrl=" + avatar + "]";
	}
}