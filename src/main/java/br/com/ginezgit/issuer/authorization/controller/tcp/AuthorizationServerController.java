package br.com.ginezgit.issuer.authorization.controller.tcp;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
@Scope("application")
public class AuthorizationServerController extends Thread {

	Logger logger = LoggerFactory.getLogger(AuthorizationServerController.class);
	
	@Autowired
    BeanFactory beanFactory;
	
	@Autowired
	public AuthorizationServer server;
	
	private ArrayList<AuthorizationHandler> handlers = new ArrayList<AuthorizationHandler>();
	
	private Boolean shouldRun = Boolean.TRUE;
	
	private boolean shouldKeepRunning() {
		synchronized (shouldRun) {
			return shouldRun.booleanValue();
		}
	}
	
	public void stopRunning() {
		synchronized (shouldRun) {
			this.shouldRun = Boolean.FALSE;
		}
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void initialize()  {
		logger.info("Starting Authorization Controller");
		this.server.start();
		this.start();
	}
	
	@Override
	public void run()  {
		while(shouldKeepRunning()) {
			Optional<Socket> newConnectionOptional = server.getNextConnection();
			if(newConnectionOptional.isPresent()) {
				AuthorizationHandler handler = createHandlerFor(newConnectionOptional.get());
				registerHandler(handler);
				handler.start();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			checkServerHealth();
			checkConnectionsHealth();
		}
	}

	private void checkServerHealth() {
		logger.debug("Authorization Server: RUNNING["+this.server.isUp()+"]");
		if(!this.server.isUp()) {
			this.server.restartListener();
		}
	}
	
	private void checkConnectionsHealth() {
		ArrayList<AuthorizationHandler> cloneHandlers = (ArrayList<AuthorizationHandler>) this.handlers.clone();
		
		for(AuthorizationHandler handler : cloneHandlers) {
			if(!handler.isUp()) {
				handler.stopRunning();
				this.handlers.remove(handler);
			}
		}
//		cloneHandlers.stream().forEach(handler -> {
//			
//		});
	}

	private AuthorizationHandler createHandlerFor(Socket newConnection) {
		AuthorizationHandler handler = beanFactory.getBean(AuthorizationHandler.class);
		handler.initialize(newConnection);
		return handler;
	}

	private void registerHandler(AuthorizationHandler handler) {
		this.handlers.add(handler);
	}
	
}
