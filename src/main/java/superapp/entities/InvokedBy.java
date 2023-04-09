package superapp.entities;

public class InvokedBy {
	UserId userID;

	public InvokedBy() {
		super();

	}
	
	public InvokedBy(UserId userID) {
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
		return "InvokedBy [userID=" + userID + "]";
	}
	
	
}	
