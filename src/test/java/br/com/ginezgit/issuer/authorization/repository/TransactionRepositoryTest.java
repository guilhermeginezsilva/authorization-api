package br.com.ginezgit.issuer.authorization.repository;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ginezgit.issuer.authorization.model.Card;
import br.com.ginezgit.issuer.authorization.model.Transaction;
import br.com.ginezgit.issuer.authorization.model.TransactionAction;
import br.com.ginezgit.issuer.authorization.model.TransactionId;
import br.com.ginezgit.issuer.authorization.model.TransactionResponseCode;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionRepositoryTest {

	@Autowired
    private TransactionRepository transactionRepository;
	
	@After
	public void afterTests() {
		transactionRepository.deleteAll();
	}
	
	private Transaction transaction1;
	private Transaction transaction2;
	private Transaction transaction3;
	private Transaction transaction4;
	private Transaction transaction5;
	private Transaction transaction6;
	private Transaction transaction7;
	private Transaction transaction8;
	private Transaction transaction9;
	private Transaction transaction10;
	private Transaction transaction11;
	
	@Before
	public void beforeTests() {
		this.transaction1 = new Transaction(new TransactionId(1, new Date()), "1234567890123456", BigDecimal.TEN, "000000", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction2 = new Transaction(new TransactionId(2, new Date()), "1234567890123456", BigDecimal.TEN, "000001", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction3 = new Transaction(new TransactionId(3, new Date()), "1234567890123456", BigDecimal.TEN, "000002", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction4 = new Transaction(new TransactionId(4, new Date()), "1234567890123456", BigDecimal.TEN, "000003", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction5 = new Transaction(new TransactionId(5, new Date()), "1234567890123456", BigDecimal.TEN, "000004", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction6 = new Transaction(new TransactionId(6, new Date()), "1234567890123456", BigDecimal.TEN, "000005", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction7 = new Transaction(new TransactionId(7, new Date()), "1234567890123456", BigDecimal.TEN, "000006", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction8 = new Transaction(new TransactionId(8, new Date()), "1234567890123456", BigDecimal.TEN, "000007", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction9 = new Transaction(new TransactionId(9, new Date()), "1234567890123456", BigDecimal.TEN, "000008", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction10 = new Transaction(new TransactionId(10, new Date()), "1234567890123456", BigDecimal.TEN, "000009", TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
		this.transaction11 = new Transaction(new TransactionId(11, new Date()), "1234567890123456", BigDecimal.TEN, null, TransactionResponseCode.APROVED, TransactionAction.WITHDRAW);
	}
	
	private void saveAll() {
		transactionRepository.save(this.transaction1);
		transactionRepository.save(this.transaction2);
		transactionRepository.save(this.transaction3);
		transactionRepository.save(this.transaction4);
		transactionRepository.save(this.transaction5);
		transactionRepository.save(this.transaction6);
		transactionRepository.save(this.transaction7);
		transactionRepository.save(this.transaction8);
		transactionRepository.save(this.transaction9);
		transactionRepository.save(this.transaction10);
		transactionRepository.save(this.transaction11);
	}
	
	@Test
	public void Should_ReturnMaximumTop10Transaction_When_ThereAreLessThan10Transactions() {
		transactionRepository.save(this.transaction1);
		transactionRepository.save(this.transaction2);
		transactionRepository.save(this.transaction3);
		transactionRepository.save(this.transaction4);
		transactionRepository.save(this.transaction5);
		transactionRepository.save(this.transaction6);
		
		List<Transaction> responseTransactions = transactionRepository.findTop10ByCardNumberOrderByIdNsuDesc("1234567890123456");
	 
	    Assert.assertEquals(6, responseTransactions.size());
	    Assert.assertTrue(responseTransactions.contains(this.transaction1));
	    Assert.assertTrue(responseTransactions.contains(this.transaction6));
	    Assert.assertFalse(responseTransactions.contains(this.transaction7));
	}
	
	@Test
	public void Should_ReturnTop10Transaction_When_MoreThan10TransactionsExists() {
		saveAll();
		
		List<Transaction> responseTransactions = transactionRepository.findTop10ByCardNumberOrderByIdNsuDesc("1234567890123456");
	 
	    Assert.assertEquals(10, responseTransactions.size());
	    Assert.assertTrue(responseTransactions.contains(this.transaction11));
	    Assert.assertFalse(responseTransactions.contains(this.transaction1));
	}
	
	@Test
	public void Should_ReturnLastExistingAuthorizationCode_When_ThereAreNullAuthorizationCodeRecords() {
		saveAll();
		
		Optional<Transaction> responseTransaction = transactionRepository.findTopByAuthorizationCodeNotNullOrderByAuthorizationCodeDesc();
	 
	    Assert.assertTrue(responseTransaction.isPresent());
	    Assert.assertEquals(this.transaction10, responseTransaction.get());
	}
	
	@Test
	public void Should_ReturnEmptyTransaction_When_ThereAreOnlyNullAuthorizationCodeRecords() {
		transactionRepository.save(this.transaction11);
		
		Optional<Transaction> responseTransaction = transactionRepository.findTopByAuthorizationCodeNotNullOrderByAuthorizationCodeDesc();
	 
	    Assert.assertFalse(responseTransaction.isPresent());
	}
	
	@Test
	public void Should_ReturnLastExistingNsu_When_ThereAreTransactionRecords() {
		saveAll();
		
		Optional<Transaction> responseTransaction = transactionRepository.findTopById_NsuNotNullOrderById_NsuDesc();
	 
	    Assert.assertTrue(responseTransaction.isPresent());
	    Assert.assertEquals(this.transaction11, responseTransaction.get());
	}
	
	@Test
	public void Should_ReturnLastExistingNsu_When_ThereArentTransactionRecords() {
		Optional<Transaction> responseTransaction = transactionRepository.findTopById_NsuNotNullOrderById_NsuDesc();
	 
	    Assert.assertFalse(responseTransaction.isPresent());
	}
}
