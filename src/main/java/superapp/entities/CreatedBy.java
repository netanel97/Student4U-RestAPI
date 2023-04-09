package superapp.entities;

public class CreatedBy {
	private UserId userID;

	public CreatedBy() {
		super();
	}

	public CreatedBy(UserId userID) {
		super();
		this.userID = userID;
	}

	public UserId getUserID() {
		return userID;
	}

	public void setUserID(UserId userID) {
		this.userID = userID;
	}

	@Override
	public String toString() {
		return "CreatedBy [userID=" + userID + "]";
	}

	
}
