package Journey.Together.exception.model;

import Journey.Together.exception.Error;
public class BadRequestException extends CustomException{
	public BadRequestException(Error error, String message) {
		super(error, message);
	}

}
