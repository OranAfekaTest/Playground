package playground.logic.service.user;

public class UserAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -1064533428097741375L;

	public UserAlreadyExistsException() {
	}

	public UserAlreadyExistsException(String message) {
		super(message);
	}

	public UserAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public UserAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

}
