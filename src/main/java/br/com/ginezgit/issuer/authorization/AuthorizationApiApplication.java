package br.com.ginezgit.issuer.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.ginezgit.issuer.authorization.controller.tcp.AuthorizationServerController;

@SpringBootApplication
public class AuthorizationApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationApiApplication.class, args);
	}
	
}
