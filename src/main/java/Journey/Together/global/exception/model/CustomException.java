package Journey.Together.global.exception.model;

import Journey.Together.global.exception.Error;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
	private final Error error;

	public CustomException(Error error, String message) {
		super(message);
		this.error = error;
	}

	public int getHttpStatus() {
		return error.getErrorCode();
	}
}
