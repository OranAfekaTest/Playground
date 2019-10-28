package playground.logic.service.user;

public class UserNotFoundException extends Exception {
	
	private static final long serialVersionUID = -4201181011658863183L;
	
	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

}
