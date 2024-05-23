package Journey.Together.global.exception.model;

import Journey.Together.global.exception.Error;

public class BadRequestException extends CustomException {
	public BadRequestException(Error error, String message) {
		super(error, message);
	}

}
