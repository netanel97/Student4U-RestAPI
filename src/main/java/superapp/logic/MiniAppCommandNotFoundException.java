package superapp.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class MiniAppCommandNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3868555986467855919L;
	
	public MiniAppCommandNotFoundException() {
	}
	
	public MiniAppCommandNotFoundException(String message) {
        super(message);
    }

    public MiniAppCommandNotFoundException(Throwable cause) {
        super(cause);
    }

    public MiniAppCommandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}