package playground.logic.service.activity;

public class ActivityAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 7027679142274290452L;

	public ActivityAlreadyExistsException() {
	}

	public ActivityAlreadyExistsException(String message) {
		super(message);
	}

	public ActivityAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public ActivityAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
