package playground.logic.service.user;

public class UserUnverifiedException extends RuntimeException {

	private static final long serialVersionUID = 1379924970772207727L;
	
	public UserUnverifiedException() {
		super();
	}

	public UserUnverifiedException(String message) {
		super(message);
	}

	public UserUnverifiedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserUnverifiedException(Throwable cause) {
		super(cause);
	}
}
