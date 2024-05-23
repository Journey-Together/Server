package Journey.Together.global.exception.model;

import Journey.Together.global.exception.Error;

public class NotFoundException extends CustomException {
	public NotFoundException(Error error, String message) {
		super(error, message);
	}
}
