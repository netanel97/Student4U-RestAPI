package entities;

public class NewUserBoundary {
	
	private eUserRole role;
	private String newUserName;
	private String avatarUrl;
	private String email;
	
	public NewUserBoundary() {
		super();
	}
	
	
	public NewUserBoundary(String email, eUserRole role, String userName, String avatarUrl) {
		super();
		this.email = email;
		this.role = role;
		this.newUserName = userName;
		this.avatarUrl = avatarUrl;
	}


	public String getEmail() {
		return email;
	}
	public eUserRole getRole() {
		return role;
	}
	public String getNewUserName() {
		return newUserName;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	public void setRole(eUserRole role) {
		this.role = role;
	}
	public void setNewUserName(String userName) {
		this.newUserName = userName;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}


	@Override
	public String toString() {
		return "UserBoundary [email=" + email + ", role=" + role + ", userName=" + newUserName + ", avatarUrl="
				+ avatarUrl + "]";
	}
	
	
	
}
