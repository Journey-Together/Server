package Journey.Together.exception.model.net;

import lombok.Getter;

import java.io.IOException;
import Journey.Together.exception.Error;

@Getter
public class CustomJavaNetException extends IOException {
	private final Error error;

	public CustomJavaNetException(Error error, String message) {
		super(message);
		this.error = error;
	}

	public int getHttpStatus() {
		return error.getErrorCode();
	}

}
