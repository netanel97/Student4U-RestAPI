package superapp.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class SuperAppObjectNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 4736835635349362027L;

	public SuperAppObjectNotFoundException() {
    }

    public SuperAppObjectNotFoundException(String message) {
        super(message);
    }

    public SuperAppObjectNotFoundException(Throwable cause) {
        super(cause);
    }

    public SuperAppObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
