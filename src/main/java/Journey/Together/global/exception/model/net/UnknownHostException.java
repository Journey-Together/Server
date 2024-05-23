package Journey.Together.global.exception.model.net;

import Journey.Together.global.exception.Error;

public class UnknownHostException extends CustomJavaNetException {
	public UnknownHostException(Error error, String message) {
		super(error, message);
	}

}
