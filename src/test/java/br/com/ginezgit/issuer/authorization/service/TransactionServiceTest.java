package br.com.ginezgit.issuer.authorization.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ginezgit.issuer.authorization.exception.CardNotFoundException;
import br.com.ginezgit.issuer.authorization.model.Card;
import br.com.ginezgit.issuer.authorization.model.Transaction;
import br.com.ginezgit.issuer.authorization.model.TransactionAction;
import br.com.ginezgit.issuer.authorization.model.TransactionId;
import br.com.ginezgit.issuer.authorization.model.TransactionResponseCode;
import br.com.ginezgit.issuer.authorization.model.controller.lasttransaction.LastTransactionsResponse;
import br.com.ginezgit.issuer.authorization.model.controller.lasttransaction.LastTransactionsResponseTransaction;
import br.com.ginezgit.issuer.authorization.model.controller.transaction.TransactionRequest;
import br.com.ginezgit.issuer.authorization.model.controller.transaction.TransactionResponse;
import br.com.ginezgit.issuer.authorization.repository.CardRepository;
import br.com.ginezgit.issuer.authorization.repository.TransactionRepository;
import br.com.ginezgit.issuer.authorization.util.UniqueCodeGenerator;

@RunWith(SpringRunner.class)
public class TransactionServiceTest {

	@TestConfiguration
    static class TransactionServiceContextConfiguration {
  
        @Bean
        public TransactionService transactionService() {
            return new TransactionService();
        }
    }

	private Card cardWithoutTransactions;
	private Card cardWithTransactions;
	private Card noExistentCard;
	
	private Transaction transaction1;
	private Transaction transaction2;
	private Transaction transaction3;
	private Transaction transaction4;
	
	private ArrayList<Transaction> transactions;
	
	@Before
	public void prepareTest() {
		this.cardWithoutTransactions = new Card("1234567890123456", BigDecimal.TEN, "Card Without Transactions");
		this.cardWithTransactions = new Card("1234567890123457", BigDecimal.TEN, "Card With Transactions");
		this.noExistentCard = new Card("1234567890123450", null, null);
		
		this.transaction1 = new Transaction(new TransactionId(1, new Date()), cardWithTransactions.getCardNumber(), BigDecimal.ONE, "000000", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction2 = new Transaction(new TransactionId(2, new Date()), cardWithTransactions.getCardNumber(), BigDecimal.ONE, "000000", TransactionResponseCode.INSUFICIENT_FUNDS, TransactionAction.WITHDRAW);
		this.transaction3 = new Transaction(new TransactionId(3, new Date()), cardWithTransactions.getCardNumber(), BigDecimal.ONE, "000000", TransactionResponseCode.INVALID_ACCOUNT, TransactionAction.WITHDRAW);
		this.transaction4 = new Transaction(new TransactionId(3, new Date()), cardWithTransactions.getCardNumber(), BigDecimal.ONE, "000000", TransactionResponseCode.PROCESSMENT_ERROR, TransactionAction.WITHDRAW);
		
		transactions = new ArrayList<Transaction>();
		transactions.add(transaction1);
		transactions.add(transaction2);
		transactions.add(transaction3);
		transactions.add(transaction4);
	}
	
	@Autowired
	private TransactionService transactionService;
	@MockBean
	private TransactionRepository transactionRespository;
	@MockBean
	private CardRepository cardRepository;
	@MockBean
	private UniqueCodeGenerator authorizationCodeGenerator;
	
	@Test
	public void Should_ReturnLastTransactionsAndCardBalance_When_CardExistsAndHasTransactions() throws Exception {
		Mockito.doReturn(this.transactions).when(transactionRespository).findTop10ByCardNumberOrderByIdNsuDesc(cardWithTransactions.getCardNumber());
		Mockito.doReturn(Optional.ofNullable(this.cardWithTransactions)).when(cardRepository).findTop1ByCardNumber(cardWithTransactions.getCardNumber());
		
		LastTransactionsResponse response = transactionService.getLast10Transactions(cardWithTransactions.getCardNumber());
		Assert.assertEquals(cardWithTransactions.getBalance(), response.getBalance());
		Assert.assertEquals(cardWithTransactions.getCardNumber(), response.getCardNumber());
		Assert.assertEquals(this.transactions.size(), response.getTransactions().size());
	}
	
	@Test
	public void Should_ReturnNoTransactionsAndCardBalance_When_CardExistsAndDoesntHasTransactions() throws Exception {
		Mockito.doReturn(new ArrayList<Transaction>()).when(transactionRespository).findTop10ByCardNumberOrderByIdNsuDesc(cardWithoutTransactions.getCardNumber());
		Mockito.doReturn(Optional.ofNullable(this.cardWithoutTransactions)).when(cardRepository).findTop1ByCardNumber(cardWithoutTransactions.getCardNumber());
		
		LastTransactionsResponse response = transactionService.getLast10Transactions(cardWithoutTransactions.getCardNumber());
		Assert.assertEquals(cardWithoutTransactions.getBalance(), response.getBalance());
		Assert.assertEquals(cardWithoutTransactions.getCardNumber(), response.getCardNumber());
		Assert.assertEquals(0, response.getTransactions().size());
	}
	
	@Test(expected=CardNotFoundException.class)
	public void Should_ThrowCardNotFoundException_When_CardDoesntExistOnFindTop10Cards() throws Exception {
		Mockito.doReturn(new ArrayList<Transaction>()).when(transactionRespository).findTop10ByCardNumberOrderByIdNsuDesc(cardWithoutTransactions.getCardNumber());
		Mockito.doReturn(Optional.empty()).when(cardRepository).findTop1ByCardNumber(cardWithoutTransactions.getCardNumber());
		
		transactionService.getLast10Transactions(cardWithoutTransactions.getCardNumber());
	}
	
	@Test
	public void Should_ReturnAprovedWithdrawResponse_When_AllConditionsAreOk() throws Exception {
		BigDecimal originalCardBalance = cardWithTransactions.getBalance();
		
		Mockito.doReturn("000000").when(authorizationCodeGenerator).generateAuthorizationCode();
		Mockito.doReturn(0).when(authorizationCodeGenerator).generateNsu();
		
		Mockito.doReturn(Optional.ofNullable(this.cardWithTransactions)).when(cardRepository).findTop1ByCardNumber(cardWithTransactions.getCardNumber());
		Mockito.when(transactionRespository.save(ArgumentMatchers.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
		Mockito.when(cardRepository.save(ArgumentMatchers.any(Card.class))).thenAnswer(i -> i.getArguments()[0]);
		
		TransactionRequest transactionRequest = new TransactionRequest(TransactionAction.WITHDRAW, this.cardWithTransactions.getCardNumber(), BigDecimal.TEN);
		TransactionResponse response = transactionService.withdraw(transactionRequest);
		
		Assert.assertEquals(cardWithTransactions.getBalance(), originalCardBalance.subtract(transactionRequest.getAmount()));
		Assert.assertEquals("000000", response.getAuthorizationCode());
		Assert.assertEquals(TransactionResponseCode.APROVED, response.getResponseCode());
		Assert.assertEquals(TransactionAction.WITHDRAW, response.getAction());
	}
	
	@Test
	public void Should_ReturnInsuficientFundsTransactionResponse_When_CardBalanceCantAffordTransactionAmount() throws Exception {
		BigDecimal originalCardBalance = cardWithTransactions.getBalance();
		
		Mockito.doReturn(0).when(authorizationCodeGenerator).generateNsu();
		
		Mockito.doReturn(Optional.ofNullable(this.cardWithTransactions)).when(cardRepository).findTop1ByCardNumber(cardWithTransactions.getCardNumber());
		Mockito.when(transactionRespository.save(ArgumentMatchers.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
		Mockito.when(cardRepository.save(ArgumentMatchers.any(Card.class))).thenAnswer(i -> i.getArguments()[0]);
		
		TransactionRequest transactionRequest = new TransactionRequest(TransactionAction.WITHDRAW, this.cardWithTransactions.getCardNumber(), new BigDecimal(100));
		
		transactionService.withdraw(transactionRequest);
		TransactionResponse response = transactionService.withdraw(transactionRequest);
		
		Assert.assertEquals(cardWithTransactions.getBalance(), originalCardBalance);
		Assert.assertNull(response.getAuthorizationCode());
		Assert.assertEquals(TransactionResponseCode.INSUFICIENT_FUNDS, response.getResponseCode());
		Assert.assertEquals(TransactionAction.WITHDRAW, response.getAction());
	}
	
	@Test
	public void Should_ReturnInvalidAccountTransactionResponse_When_CardDoesntExistOnWithdraw() throws Exception {
		BigDecimal originalCardBalance = cardWithTransactions.getBalance();
		
		Mockito.doReturn(0).when(authorizationCodeGenerator).generateNsu();
		Mockito.doReturn(Optional.empty()).when(cardRepository).findTop1ByCardNumber(noExistentCard.getCardNumber());
		
		TransactionRequest transactionRequest = new TransactionRequest(TransactionAction.WITHDRAW, this.noExistentCard.getCardNumber(), BigDecimal.TEN);
		
		transactionService.withdraw(transactionRequest);
		TransactionResponse response = transactionService.withdraw(transactionRequest);
		
		Assert.assertEquals(cardWithTransactions.getBalance(), originalCardBalance);
		Assert.assertNull(response.getAuthorizationCode());
		Assert.assertEquals(TransactionResponseCode.INVALID_ACCOUNT, response.getResponseCode());
		Assert.assertEquals(TransactionAction.WITHDRAW, response.getAction());
	}
	
	@Test
	public void Should_ReturnProcessmentErrorTransactionResponse_When_SomeUnhandledExceptionIsThrown() throws Exception {
		BigDecimal originalCardBalance = cardWithTransactions.getBalance();
		
		Mockito.doReturn(0).when(authorizationCodeGenerator).generateNsu();
		Mockito.doReturn(Optional.ofNullable(this.cardWithTransactions)).when(cardRepository).findTop1ByCardNumber(cardWithTransactions.getCardNumber());
		Mockito.doThrow(new RuntimeException()).when(cardRepository).save(ArgumentMatchers.any(Card.class));
		
		TransactionRequest transactionRequest = new TransactionRequest(TransactionAction.WITHDRAW, this.cardWithTransactions.getCardNumber(), BigDecimal.TEN);
		
		TransactionResponse response = transactionService.withdraw(transactionRequest);
		
		Assert.assertEquals(cardWithTransactions.getBalance(), originalCardBalance.subtract(transactionRequest.getAmount()));
		Assert.assertNull(response.getAuthorizationCode());
		Assert.assertEquals(TransactionResponseCode.PROCESSMENT_ERROR, response.getResponseCode());
		Assert.assertEquals(TransactionAction.WITHDRAW, response.getAction());
	}
}
