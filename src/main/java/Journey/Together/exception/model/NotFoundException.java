package Journey.Together.exception.model;

import Journey.Together.exception.Error;
public class NotFoundException extends CustomException {
	public NotFoundException(Error error, String message) {
		super(error, message);
	}
}
