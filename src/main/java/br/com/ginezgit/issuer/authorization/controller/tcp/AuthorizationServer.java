package br.com.ginezgit.issuer.authorization.controller.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.ginezgit.issuer.authorization.exception.AuthorizationServerException;
import br.com.ginezgit.issuer.authorization.util.Instance;

@Component
@Scope("singleton")
public class AuthorizationServer extends Thread {
	Logger logger = LoggerFactory.getLogger(AuthorizationServer.class);
	
	private ServerSocket server;
	
	@Value("${authorization.endpoint.port:8081}")
	private Integer listenerPort;
	@Value("${authorization.endpoint.host:127.0.0.1}")
	private String listenerHost;
	
	private LinkedList<Socket> connectionsQueue = new LinkedList<Socket>();
	
	private Boolean shouldRun = Boolean.TRUE;
	
	@Override
	public void run() {
		logger.info("Starting Authorization Server");
		this.startListener();
		
		while(shouldKeepRunning()) {
			Optional<Socket> connectionSocketOptional = waitForNextConnection();
			if(connectionSocketOptional.isPresent()) {
				this.addConnectionToQueue(connectionSocketOptional.get());
			}
		}
		logger.info("Stopping Authorization Server");
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
	
	public void restartListener() {
		this.disconnect();
		this.startListener();
	}
	
	public void startListener() {
		try {
			if(!Instance.isEmpty(server)) {
				this.disconnect();
			}
		
			InetSocketAddress socketAddress = new InetSocketAddress(listenerHost, listenerPort);
			this.server = new ServerSocket(socketAddress.getPort(), 0, socketAddress.getAddress());

			logger.info("Authorization Endpoint started on port: " + server.getLocalPort());
		} catch (IOException e) {
			String message = "An error has ocurred during Authorization Endpoin Starting";
			logger.error(message, e);
			throw new AuthorizationServerException(message);
		}
	}
	
	private Optional<Socket> waitForNextConnection() {
		try {
			return Optional.ofNullable(this.server.accept());
		} catch (IOException e) {
			String message = "An error has ocurred when trying to get next Authorization Endpoint connection";
			logger.error(message, e);
			this.disconnect();
			throw new AuthorizationServerException(message);
		}
	}
	
	private void disconnect() {
		try {
			if(!this.server.isClosed()) {
				this.server.close();
			}
		} catch (IOException e) {
			String message = "An error has ocurred when trying to close Authorization Endpoint server";
			logger.error(message, e);
			throw new AuthorizationServerException(message);
		}
	}
	
	public Optional<Socket> getNextConnection() {
		return pollNextConnectionFromQueue();
	}
	
	public boolean isUp() {
		return this.server.isBound() && this.isAlive();
	}
	
	private void addConnectionToQueue(Socket connectionSocket) {
		synchronized (connectionsQueue) {
			this.connectionsQueue.addLast(connectionSocket);
		}
	}
	
	private Optional<Socket> pollNextConnectionFromQueue() {
		synchronized (connectionsQueue) {
			return Optional.ofNullable(this.connectionsQueue.pollFirst());
		}
	}
	
	
}
