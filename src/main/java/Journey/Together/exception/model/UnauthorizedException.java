package Journey.Together.exception.model;

import Journey.Together.exception.Error;
public class UnauthorizedException extends CustomException{
	public UnauthorizedException(Error error, String message) {
		super(error, message);
	}

}
