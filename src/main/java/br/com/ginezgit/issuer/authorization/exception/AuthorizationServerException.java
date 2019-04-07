package br.com.ginezgit.issuer.authorization.exception;

public class AuthorizationServerException extends RuntimeException {

	public AuthorizationServerException() {
		super();
	}

	public AuthorizationServerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AuthorizationServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthorizationServerException(String message) {
		super(message);
	}

	public AuthorizationServerException(Throwable cause) {
		super(cause);
	}

	
	
}
