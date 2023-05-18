package superapp.logic;

public class SuperAppObjectNotActiveException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4698641013377843751L;
	public SuperAppObjectNotActiveException() {
    }

    public SuperAppObjectNotActiveException(String message) {
        super(message);
    }

    public SuperAppObjectNotActiveException(Throwable cause) {
        super(cause);
    }

    public SuperAppObjectNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
