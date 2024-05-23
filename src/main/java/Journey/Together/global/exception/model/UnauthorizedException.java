package Journey.Together.global.exception.model;

import Journey.Together.global.exception.Error;

public class UnauthorizedException extends CustomException {
	public UnauthorizedException(Error error, String message) {
		super(error, message);
	}

}
