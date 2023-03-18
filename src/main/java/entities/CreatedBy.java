package entities;

public class CreatedBy {
	private UserID userID;

	public CreatedBy() {
		super();
	}

	public CreatedBy(UserID userID) {
		super();
		this.userID = userID;
	}

	public UserID getUserID() {
		return userID;
	}

	public void setUserID(UserID userID) {
		this.userID = userID;
	}

	@Override
	public String toString() {
		return "CreatedBy [userID=" + userID + "]\n";
	}

	
}
