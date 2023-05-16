package superapp.logic.mongo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DepreacatedOpterationException extends RuntimeException {

	private static final long serialVersionUID = -1069504950425401618L;

	

	public DepreacatedOpterationException() {
	}

	public DepreacatedOpterationException(String message) {
		super(message);
	}

	public DepreacatedOpterationException(Throwable cause) {
		super(cause);
	}

	public DepreacatedOpterationException(String message, Throwable cause) {
		super(message, cause);
	}
}
