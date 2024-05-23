package Journey.Together.global.exception.model;

import Journey.Together.global.exception.Error;

public class ForbiddenException extends CustomException {
	public ForbiddenException(Error error, String message) {
		super(error, message);
	}

}