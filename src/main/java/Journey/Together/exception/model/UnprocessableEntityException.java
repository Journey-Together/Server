package Journey.Together.exception.model;

import Journey.Together.exception.Error;

public class UnprocessableEntityException extends CustomException{
	public UnprocessableEntityException(Error error, String message) {
		super(error, message);
	}
}
