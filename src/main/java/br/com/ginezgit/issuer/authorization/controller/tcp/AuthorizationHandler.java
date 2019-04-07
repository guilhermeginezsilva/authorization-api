package br.com.ginezgit.issuer.authorization.controller.tcp;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import br.com.ginezgit.issuer.authorization.exception.AuthorizationHandlerException;
import br.com.ginezgit.issuer.authorization.exception.AuthorizationServerException;
import br.com.ginezgit.issuer.authorization.model.controller.transaction.TransactionRequest;
import br.com.ginezgit.issuer.authorization.model.controller.transaction.TransactionResponse;
import br.com.ginezgit.issuer.authorization.service.TransactionService;
import br.com.ginezgit.issuer.authorization.util.StringHex;

@Component
@Scope("prototype")
public class AuthorizationHandler extends Thread {
	Logger logger = LoggerFactory.getLogger(AuthorizationServer.class);
	
	private static final int DEFAULT_MESSAGE_LENGTH_FIELD_LENGTH = 4;
	
	@Autowired
	private TransactionService transactionService;
	
	private Socket connection;
	private Boolean shouldRun = Boolean.TRUE;
	
	private int transCount = 0;
	
	public void initialize(Socket connection) {
		this.connection = connection;
	}
	
	@Override
	public void run() {
		logger.info("Starting Authorization Handler Local(host:"+connection.getLocalSocketAddress()+" port:"+connection.getLocalPort()+") Remote(host:"+connection.getRemoteSocketAddress()+" port:"+connection.getPort()+")");
		while(shouldKeepRunning()) {
			try {
				Optional<String> messageOptional = getNextConnectionMessage();
				
				long startTime = System.currentTimeMillis();
				
				if(!messageOptional.isPresent()) {
					continue;
				}
				
				ObjectMapper mapper = new ObjectMapper();
				TransactionRequest transactionRequest = mapper.readValue(messageOptional.get(), TransactionRequest.class);
				
				TransactionResponse transactionResponse = transactionService.withdraw(transactionRequest);
				
				String transactionResponseJsonStr = mapper.writeValueAsString(transactionResponse);
				
				sendResponse(transactionResponseJsonStr);
				
				long endTime = System.currentTimeMillis();
				
				logger.info(endTime-startTime+" ms - " + transCount++ + " - " + transactionResponseJsonStr);
				
			} catch (InvalidFormatException e) {
				logger.debug(e.getMessage(), e);
				this.disconnect();
			} catch (JsonParseException e) {
				logger.debug(e.getMessage(), e);
				this.disconnect();
			} catch (JsonMappingException e) {
				logger.error(e.getMessage(), e);
				this.disconnect();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				this.disconnect();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				this.disconnect();
			}
		}
		logger.info("Stopping Authorization Handler Local(host:"+connection.getLocalSocketAddress()+" port:"+connection.getLocalPort()+") Remote(host:"+connection.getRemoteSocketAddress()+" port:"+connection.getPort()+")" );
	}
	
	private Optional<String> getNextConnectionMessage() {
		try {
			byte[] messageLengthBytes = new byte[DEFAULT_MESSAGE_LENGTH_FIELD_LENGTH];
			connection.getInputStream().read(messageLengthBytes, 0, DEFAULT_MESSAGE_LENGTH_FIELD_LENGTH);
			
			String messageLengthStr = new String(messageLengthBytes, StandardCharsets.UTF_8);
			if(!messageLengthStr.matches("^[0-9]+")) {
				connection.getInputStream().skip(connection.getInputStream().available());
				return Optional.empty();
			}
			int messageLengthInt = Integer.parseInt(messageLengthStr);
			
			byte[] messageContentBytes = new byte[messageLengthInt];
			connection.getInputStream().read(messageContentBytes, 0, messageLengthInt);
			
			String messageContentStrHex = new String(messageContentBytes, StandardCharsets.UTF_8);
			return Optional.ofNullable(StringHex.hexToStr(messageContentStrHex));
		} catch(Exception e) {
			String message = "An error has ocurred when trying to read from authorization client connection, connection is being closed.";
			logger.error(message, e);
			this.stopRunning();
			throw new AuthorizationHandlerException(message);
		}
	}
	
	private void sendResponse(String transactionResponseJsonStr) {
		try {
			String messageContentStrHex = StringHex.strToHex(transactionResponseJsonStr);
			String paddedMessageLengthStr = String.format("%04d" , messageContentStrHex.length());
			
			String message = paddedMessageLengthStr+messageContentStrHex;

			connection.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
		} catch(Exception e) {
			String message = "An error has ocurred when trying to write to authorization client connection, connection is being closed.";
			logger.error(message, e);
			this.disconnect();
			throw new AuthorizationHandlerException(message);
		}
	}

	public boolean isUp() {
		return this.isAlive() && this.connection.isConnected();
	}
	
	private boolean shouldKeepRunning() {
		synchronized (shouldRun) {
			return shouldRun.booleanValue();
		}
	}
	
	public void stopRunning() {
		synchronized (shouldRun) {
			this.shouldRun = Boolean.FALSE;
		}
		this.disconnect();
	}
	
	private void disconnect() {
		try {
			if(!this.connection.isClosed()) {
				this.connection.close();
			}
		} catch (IOException e) {
			String message = "An error has ocurred when trying to close Authorization Endpoint client connection";
			logger.error(message, e);
			throw new AuthorizationServerException(message);
		}
	}

}
