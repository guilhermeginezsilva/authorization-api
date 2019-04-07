package br.com.ginezgit.issuer.authorization.exception;

public class AuthorizationHandlerException extends RuntimeException {

	public AuthorizationHandlerException() {
		super();
	}

	public AuthorizationHandlerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AuthorizationHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthorizationHandlerException(String message) {
		super(message);
	}

	public AuthorizationHandlerException(Throwable cause) {
		super(cause);
	}

	
	
}
