package playground.logic.service.activity;

public class ActivityIllegalInputException extends Exception {
	
	private static final long serialVersionUID = 843091594020525076L;
	
	public ActivityIllegalInputException() {
	}

	public ActivityIllegalInputException(String message) {
		super(message);
	}

	public ActivityIllegalInputException(Throwable cause) {
		super(cause);
	}

	public ActivityIllegalInputException(String message, Throwable cause) {
		super(message, cause);
	}

}
