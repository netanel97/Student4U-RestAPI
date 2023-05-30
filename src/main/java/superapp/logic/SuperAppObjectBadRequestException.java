package superapp.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SuperAppObjectBadRequestException extends RuntimeException{

	private static final long serialVersionUID = -572560227062390389L;

	public SuperAppObjectBadRequestException() {
    }

    public SuperAppObjectBadRequestException(String message) {
        super(message);
    }

    public SuperAppObjectBadRequestException(Throwable cause) {
        super(cause);
    }

    public SuperAppObjectBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
