package entities;

public class InvokedBy {
	UserID userID;

	public InvokedBy() {
		super();

	}
	
	public InvokedBy(UserID userID) {
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
		return "InvokedBy [userID=" + userID + "]";
	}
	
	
}	
