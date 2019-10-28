package playground.logic.service.user;

public class UserIllegalInputException extends RuntimeException {

	private static final long serialVersionUID = -8982179114343968859L;
	
	public UserIllegalInputException() {
		super();
	}

	public UserIllegalInputException(String message) {
		super(message);
	}

	public UserIllegalInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserIllegalInputException(Throwable cause) {
		super(cause);
	}
}
