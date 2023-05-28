package superapp.boundaries.command;

import superapp.boundaries.user.UserId;

public class InvokedBy {
	private UserId userId;

	public InvokedBy() {
		super();

	}

	public InvokedBy(UserId userId) {
		super();
		this.userId = userId;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "InvokedBy [userId=" + userId + "]";
	}
	
	
}	
