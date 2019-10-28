package playground.plugins;

public class MessageNotFoundException extends Exception {
	
	private static final long serialVersionUID = 7027679142274290452L;

	public MessageNotFoundException() {
	}

	public MessageNotFoundException(String message) {
		super(message);
	}

	public MessageNotFoundException(Throwable cause) {
		super(cause);
	}

	public MessageNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	

}
