package br.com.ginezgit.issuer.authorization.controller.rest;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.ginezgit.issuer.authorization.exception.CardNotFoundException;
import br.com.ginezgit.issuer.authorization.exception.AuthorizationHandlerException;
import br.com.ginezgit.issuer.authorization.exception.AuthorizationServerException;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

	Logger logger = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

	
	
	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<?> constraintViolationException(RuntimeException ex, WebRequest request) {
		logger.debug(ex.getMessage(), ex);
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.debug(ex.getMessage(), ex);
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(value = CardNotFoundException.class)
	public ResponseEntity<?> accountNotFoundException(RuntimeException ex, WebRequest request) {
		logger.debug(ex.getMessage(), ex);
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(value = AuthorizationHandlerException.class)
	public ResponseEntity<?> authorizationHandlerException(RuntimeException ex, WebRequest request) {
		String publicMessage = "Sorry, an error has ocurred, but we are already working on it; try again later.";
		logger.debug(ex.getMessage(), ex);
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, publicMessage);
	}

	@ExceptionHandler(value = AuthorizationServerException.class)
	public ResponseEntity<?> authorizationServerException(RuntimeException ex, WebRequest request) {
		String publicMessage = "Sorry, an error has ocurred, but we are already working on it; try again later.";
		logger.debug(ex.getMessage(), ex);
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, publicMessage);
	}

	private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String errorMessage) {
		return new ResponseEntity(new ErrorResponse(status, errorMessage), status);
	}

	@Data
	@AllArgsConstructor
	public class ErrorResponse {
		private HttpStatus status;
		private String errorMessage;

	}

}
