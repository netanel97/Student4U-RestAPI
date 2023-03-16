package entities;

public class UserBoundary {
	
	private UserID userId;
	private eUserRole role;
	private String userName;
	private String avatarUrl;
	
	
	public UserBoundary() {
		super();
	}
	
	
	public UserBoundary(UserID userId, eUserRole role, String userName, String avatarUrl) {
		super();
		this.userId = userId;
		this.role = role;
		this.userName = userName;
		this.avatarUrl = avatarUrl;
	}



	public UserID getUserId() {
		return userId;
	}
	public eUserRole getRole() {
		return role;
	}
	public String getUserName() {
		return userName;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setUserId(UserID userId) {
		this.userId = userId;
	}
	public void setRole(eUserRole role) {
		this.role = role;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}


	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId.toString() + ", role=" + role + ", userName=" + userName + ", avatarUrl="
				+ avatarUrl + "]";
	}
	
	
	
}
