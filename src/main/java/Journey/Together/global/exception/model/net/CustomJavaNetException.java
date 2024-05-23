package Journey.Together.global.exception.model.net;

import Journey.Together.global.exception.Error;
import lombok.Getter;

import java.io.IOException;

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
