package Journey.Together.global.exception.model;

import Journey.Together.global.exception.Error;

public class UnprocessableEntityException extends CustomException {
	public UnprocessableEntityException(Error error, String message) {
		super(error, message);
	}
}
