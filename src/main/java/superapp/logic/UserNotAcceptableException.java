package superapp.logic;

public class UserNotAcceptableException extends RuntimeException{

	private static final long serialVersionUID = 7665690495041808076L;

	
	public UserNotAcceptableException() {
	}

	public UserNotAcceptableException(String message) {
		super(message);
	}

	public UserNotAcceptableException(Throwable cause) {
		super(cause);
	}

	public UserNotAcceptableException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
