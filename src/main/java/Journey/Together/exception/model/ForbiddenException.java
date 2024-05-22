package Journey.Together.exception.model;

import Journey.Together.exception.Error;

public class ForbiddenException extends CustomException{
	public ForbiddenException(Error error, String message) {
		super(error, message);
	}

}