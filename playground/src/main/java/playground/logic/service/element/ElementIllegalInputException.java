package playground.logic.service.element;

public class ElementIllegalInputException extends RuntimeException {

	private static final long serialVersionUID = -1388875169939999015L;

	public ElementIllegalInputException() {
		super();
	}

	public ElementIllegalInputException(String message) {
		super(message);
	}

	public ElementIllegalInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElementIllegalInputException(Throwable cause) {
		super(cause);
	}
}
