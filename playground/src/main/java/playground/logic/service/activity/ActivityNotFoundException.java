package playground.logic.service.activity;

public class ActivityNotFoundException extends Exception {

	private static final long serialVersionUID = 640086827252895743L;

	public ActivityNotFoundException() {
		super();
	}

	public ActivityNotFoundException(String message) {
		super(message);
	}

	public ActivityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActivityNotFoundException(Throwable cause) {
		super(cause);
	}
}
