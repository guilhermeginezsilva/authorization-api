package br.com.ginezgit.issuer.authorization.controller.tcp;

import java.net.Socket;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.junit4.SpringRunner;

import com.jayway.jsonpath.JsonPath;

import br.com.ginezgit.issuer.authorization.model.TransactionAction;
import br.com.ginezgit.issuer.authorization.model.TransactionResponseCode;
import br.com.ginezgit.issuer.authorization.model.controller.transaction.TransactionRequest;
import br.com.ginezgit.issuer.authorization.model.controller.transaction.TransactionResponse;
import br.com.ginezgit.issuer.authorization.service.TransactionService;
import br.com.ginezgit.issuer.authorization.util.SocketUtils;

@RunWith(SpringRunner.class)
public class AuthorizationHandlerTest {

	@TestConfiguration
    static class AuthorizationHandlerContextConfiguration {
  
        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        public AuthorizationHandler authorizationHandler() {
            return new AuthorizationHandler();
        }
        
        @Bean
        public AuthorizationServer authorizationServer() {
            return new AuthorizationServer();
        }
        
        @Bean
        public AuthorizationServerController authorizationServerController() {
            return new AuthorizationServerController();
        }
    }
	
	@MockBean
	private TransactionService transactionService;
	
	@Autowired
	private AuthorizationServerController authorizationServerController;
	
	@Value("${authorization.endpoint.port:8081}")
	private Integer authorizationEndpointPort;
	
	@Value("${authorization.endpoint.host:127.0.0.1}")
	private String authorizationEndpointHost;
	
	private String requestJson = "{\"action\":\"withdraw\",\"cardnumber\":\"1234567890123456\",\"amount\":\"1,10\"}";
	
	@Before
	public void prepareTest() {
		if(!authorizationServerController.isAlive()) {
			authorizationServerController.initialize();
		}
	}
	
	@Test
	public void Should_ReturnAprovedTransactionResponse_When_TransactionRequestIsOk() throws Exception {
		TransactionResponse transactionResponse = new TransactionResponse(TransactionAction.WITHDRAW, TransactionResponseCode.APROVED, "000000");
		
		BDDMockito.given(transactionService.withdraw(ArgumentMatchers.any(TransactionRequest.class))).willReturn(transactionResponse);
		
		Socket socket = new Socket(authorizationEndpointHost, authorizationEndpointPort);
		SocketUtils.send(socket, requestJson);
		Optional<String> responseJsonOptional = SocketUtils.receive(socket);
		
		Map<String, String> objectMap = JsonPath.read(responseJsonOptional.get(), "$");
		Assert.assertEquals(objectMap.get("action"), TransactionAction.WITHDRAW.getJsonValue());
		Assert.assertEquals(objectMap.get("code"), TransactionResponseCode.APROVED.getCode());
		Assert.assertEquals(objectMap.get("authorization_code"), "000000");
	}
	
	@Test
	public void Should_ReturnInsuficientFundsTransactionResponse_When_TransactionRequestCardHasInsuficientFunds() throws Exception {
		TransactionResponse transactionResponse = new TransactionResponse(TransactionAction.WITHDRAW, TransactionResponseCode.INSUFICIENT_FUNDS, null);
		
		BDDMockito.given(transactionService.withdraw(ArgumentMatchers.any(TransactionRequest.class))).willReturn(transactionResponse);
		
		Socket socket = new Socket(authorizationEndpointHost, authorizationEndpointPort);
		SocketUtils.send(socket, requestJson);
		Optional<String> responseJsonOptional = SocketUtils.receive(socket);
		
		Map<String, String> objectMap = JsonPath.read(responseJsonOptional.get(), "$");
		Assert.assertEquals(objectMap.get("action"), TransactionAction.WITHDRAW.getJsonValue());
		Assert.assertEquals(objectMap.get("code"), TransactionResponseCode.INSUFICIENT_FUNDS.getCode());
		Assert.assertEquals(objectMap.get("authorization_code"), null);
	}
	
	@Test
	public void Should_ReturnInvalidAccountTransactionResponse_When_TransactionRequestCardDoesntExists() throws Exception {
		TransactionResponse transactionResponse = new TransactionResponse(TransactionAction.WITHDRAW, TransactionResponseCode.INVALID_ACCOUNT, null);
		
		BDDMockito.given(transactionService.withdraw(ArgumentMatchers.any(TransactionRequest.class))).willReturn(transactionResponse);
		
		Socket socket = new Socket(authorizationEndpointHost, authorizationEndpointPort);
		SocketUtils.send(socket, requestJson);
		Optional<String> responseJsonOptional = SocketUtils.receive(socket);
		
		Map<String, String> objectMap = JsonPath.read(responseJsonOptional.get(), "$");
		Assert.assertEquals(objectMap.get("action"), TransactionAction.WITHDRAW.getJsonValue());
		Assert.assertEquals(objectMap.get("code"), TransactionResponseCode.INVALID_ACCOUNT.getCode());
		Assert.assertEquals(objectMap.get("authorization_code"), null);
	}
	
	@Test
	public void Should_ReturnProcessmentErrorTransactionResponse_When_OccursSomeErrorOnProcessment() throws Exception {
		TransactionResponse transactionResponse = new TransactionResponse(TransactionAction.WITHDRAW, TransactionResponseCode.PROCESSMENT_ERROR, null);
		
		BDDMockito.given(transactionService.withdraw(ArgumentMatchers.any(TransactionRequest.class))).willReturn(transactionResponse);
		
		Socket socket = new Socket(authorizationEndpointHost, authorizationEndpointPort);
		SocketUtils.send(socket, requestJson);
		Optional<String> responseJsonOptional = SocketUtils.receive(socket);
		
		Map<String, String> objectMap = JsonPath.read(responseJsonOptional.get(), "$");
		Assert.assertEquals(objectMap.get("action"), TransactionAction.WITHDRAW.getJsonValue());
		Assert.assertEquals(objectMap.get("code"), TransactionResponseCode.PROCESSMENT_ERROR.getCode());
		Assert.assertEquals(objectMap.get("authorization_code"), null);
	}
	
}
