package br.com.ginezgit.issuer.authorization.exception;

public class InvalidRequestParameters extends RuntimeException {

	public InvalidRequestParameters() {
		super();
	}

	public InvalidRequestParameters(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidRequestParameters(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidRequestParameters(String message) {
		super(message);
	}

	public InvalidRequestParameters(Throwable cause) {
		super(cause);
	}

	
	
}
