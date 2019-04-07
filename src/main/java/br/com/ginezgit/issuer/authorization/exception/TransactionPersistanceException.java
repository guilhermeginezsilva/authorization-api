package br.com.ginezgit.issuer.authorization.exception;

public class TransactionPersistanceException extends RuntimeException {

	public TransactionPersistanceException() {
		super();
	}

	public TransactionPersistanceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TransactionPersistanceException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransactionPersistanceException(String message) {
		super(message);
	}

	public TransactionPersistanceException(Throwable cause) {
		super(cause);
	}

	
	
}
