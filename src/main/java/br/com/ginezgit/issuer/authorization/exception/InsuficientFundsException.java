package br.com.ginezgit.issuer.authorization.exception;

public class InsuficientFundsException extends RuntimeException {

	public InsuficientFundsException() {
		super();
	}

	public InsuficientFundsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InsuficientFundsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InsuficientFundsException(String message) {
		super(message);
	}

	public InsuficientFundsException(Throwable cause) {
		super(cause);
	}

	
	
}
