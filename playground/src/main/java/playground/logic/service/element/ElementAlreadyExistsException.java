package playground.logic.service.element;

public class ElementAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 4646082382096574539L;

	public ElementAlreadyExistsException() {
	}

	public ElementAlreadyExistsException(String message) {
		super(message);
	}

	public ElementAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public ElementAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}
