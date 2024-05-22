package Journey.Together.exception.model.net;
import Journey.Together.exception.Error;
public class UnknownHostException extends CustomJavaNetException {
	public UnknownHostException(Error error, String message) {
		super(error, message);
	}

}
