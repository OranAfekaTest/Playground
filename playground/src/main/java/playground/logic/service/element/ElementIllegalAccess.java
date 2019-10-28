package playground.logic.service.element;

public class ElementIllegalAccess extends RuntimeException {

	private static final long serialVersionUID = -3594775370326026239L;
	
	public ElementIllegalAccess() {
	}

	public ElementIllegalAccess(String message) {
		super(message);
	}

	public ElementIllegalAccess(Throwable cause) {
		super(cause);
	}

	public ElementIllegalAccess(String message, Throwable cause) {
		super(message, cause);
	}

}
