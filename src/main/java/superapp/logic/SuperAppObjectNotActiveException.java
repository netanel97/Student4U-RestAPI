package superapp.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
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
