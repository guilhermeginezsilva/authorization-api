package br.com.ginezgit.issuer.authorization.controller.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.ginezgit.issuer.authorization.exception.CardNotFoundException;
import br.com.ginezgit.issuer.authorization.model.Card;
import br.com.ginezgit.issuer.authorization.model.Transaction;
import br.com.ginezgit.issuer.authorization.model.TransactionAction;
import br.com.ginezgit.issuer.authorization.model.TransactionId;
import br.com.ginezgit.issuer.authorization.model.TransactionResponseCode;
import br.com.ginezgit.issuer.authorization.model.controller.lasttransaction.LastTransactionsResponse;
import br.com.ginezgit.issuer.authorization.model.controller.lasttransaction.LastTransactionsResponseTransaction;
import br.com.ginezgit.issuer.authorization.service.TransactionService;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

	private static final String REST_DOMAIN = "/v1/transactions";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private TransactionService transactionService;
	
	private Card cardWithoutTransactions;
	private Card cardWithTransactions;
	private Card noExistentCard;
	
	private Transaction transaction1;
	private Transaction transaction2;
	private Transaction transaction3;
	private Transaction transaction4;
	
	private ArrayList<Transaction> transactions;
	
	private LastTransactionsResponse lastTransactionsResponseWithTransactionsAndBalanceMock;
	private LastTransactionsResponse lastTransactionsResponseWithNoTransactionsAndBalanceMock;
	
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
		
		this.lastTransactionsResponseWithTransactionsAndBalanceMock = new LastTransactionsResponse(cardWithTransactions.getCardNumber(), cardWithTransactions.getBalance(), 
				Arrays.asList(
					new LastTransactionsResponseTransaction(transaction1.getId().getDate(), transaction1.getAmount(), transaction1.getResponseCode()),
					new LastTransactionsResponseTransaction(transaction2.getId().getDate(), transaction2.getAmount(), transaction2.getResponseCode()),
					new LastTransactionsResponseTransaction(transaction3.getId().getDate(), transaction3.getAmount(), transaction3.getResponseCode()),
					new LastTransactionsResponseTransaction(transaction4.getId().getDate(), transaction4.getAmount(), transaction4.getResponseCode())
						
		));
		
		this.lastTransactionsResponseWithNoTransactionsAndBalanceMock = new LastTransactionsResponse(cardWithoutTransactions.getCardNumber(), cardWithoutTransactions.getBalance(), new ArrayList<LastTransactionsResponseTransaction>());
	}

	@Test
	public void Should_ReturnLastTransactionsAndCardBalance_When_CardExistsAndHasTransactions() throws Exception {
		BDDMockito.given(transactionService.getLast10Transactions(cardWithTransactions.getCardNumber())).willReturn(lastTransactionsResponseWithTransactionsAndBalanceMock);
		
		mvc.perform(MockMvcRequestBuilders.get(REST_DOMAIN + "/cards/"+cardWithTransactions.getCardNumber()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.cardnumber", Matchers.is(lastTransactionsResponseWithTransactionsAndBalanceMock.getCardNumber())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.availableAmount", Matchers.is(lastTransactionsResponseWithTransactionsAndBalanceMock.getBalance().intValue())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.transactions", Matchers.hasSize(4)));
	}
	
	@Test
	public void Should_ReturnNoTransactionsAndCardBalance_When_CardExistsAndDoesntHasTransactions() throws Exception {
		BDDMockito.given(transactionService.getLast10Transactions(cardWithoutTransactions.getCardNumber())).willReturn(lastTransactionsResponseWithNoTransactionsAndBalanceMock);
		
		mvc.perform(MockMvcRequestBuilders.get(REST_DOMAIN + "/cards/"+cardWithoutTransactions.getCardNumber()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.cardnumber", Matchers.is(lastTransactionsResponseWithNoTransactionsAndBalanceMock.getCardNumber())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.availableAmount", Matchers.is(lastTransactionsResponseWithNoTransactionsAndBalanceMock.getBalance().intValue())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.transactions", Matchers.hasSize(0)));
	}
	
	@Test
	public void Should_ReturnBadRequest_When_CardIsInvalid() throws Exception {
		String cardLength14 = "12345678901234";
		String cardLength20 = "12345678901234567890";
		String cardWithLetters = "12345678901234h";
		
		mvc.perform(MockMvcRequestBuilders.get(REST_DOMAIN + "/cards/"+cardLength14))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		mvc.perform(MockMvcRequestBuilders.get(REST_DOMAIN + "/cards/"+cardLength20))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		mvc.perform(MockMvcRequestBuilders.get(REST_DOMAIN + "/cards/"+cardWithLetters))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void Should_ReturnNotFound_When_CardDoesntExist() throws Exception {
		BDDMockito.given(transactionService.getLast10Transactions(noExistentCard.getCardNumber())).willThrow(new CardNotFoundException("Card number couldn't be found"));
		
		mvc.perform(MockMvcRequestBuilders.get(REST_DOMAIN + "/cards/"+noExistentCard.getCardNumber()))
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
}
