package superapp.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class DateFormatIncorrectException extends RuntimeException {
    public DateFormatIncorrectException() {
    }
    public DateFormatIncorrectException(String message) {
        super(message);
    }

    public DateFormatIncorrectException(Throwable cause) {
        super(cause);
    }

    public DateFormatIncorrectException(String message, Throwable cause) {
        super(message, cause);
    }
}
