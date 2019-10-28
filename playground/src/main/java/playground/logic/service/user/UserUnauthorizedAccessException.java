package playground.logic.service.user;

public class UserUnauthorizedAccessException extends Exception {
	
	private static final long serialVersionUID = 5087715617945215124L;

	public UserUnauthorizedAccessException() {
		super();
	}

	public UserUnauthorizedAccessException(String message) {
		super(message);
	}

	public UserUnauthorizedAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserUnauthorizedAccessException(Throwable cause) {
		super(cause);
	}

}