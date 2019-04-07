package br.com.ginezgit.issuer.authorization.repository;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
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

@RunWith(SpringRunner.class)
@DataJpaTest
public class CardRepositoryTest {

	@Autowired
    private CardRepository cardRepository;
	
	@After
	public void afterTests() {
		cardRepository.deleteAll();
	}
	
	private Card card1;
	private Card card2;
	private Card card3;
	private Card card4;
	
	@Before
	public void beforeTests() {
		this.card1 = new Card("1234567890123456", BigDecimal.TEN, "Card 1");
		this.card2 = new Card("1234567890123457", BigDecimal.TEN, "Card 2");
		this.card3 = new Card("1234567890123458", BigDecimal.TEN, "Card 3");
		this.card4 = new Card("1234567890123459", BigDecimal.TEN, "Card 4");
	}
	
	@Test
	public void Should_ReturnOneCard_When_CardExists() {
		cardRepository.save(this.card1);
		
		Optional<Card> responseCard = cardRepository.findTop1ByCardNumber(this.card1.getCardNumber());
	 
	    assertTrue(responseCard.isPresent());
	    Assert.assertEquals(this.card1.getCardNumber(), responseCard.get().getCardNumber());
	}
	
	@Test
	public void Should_ReturnOneCard_When_MoreThanOneCardExists() {
		cardRepository.save(this.card1);
		cardRepository.save(this.card2);
		cardRepository.save(this.card3);
		cardRepository.save(this.card4);
		
		Optional<Card> responseCard = cardRepository.findTop1ByCardNumber(this.card1.getCardNumber());
	 
	    assertTrue(responseCard.isPresent());
	    Assert.assertEquals(this.card1.getCardNumber(), responseCard.get().getCardNumber());
	}
	
	@Test
	public void Should_ReturnNoCard_When_CardDoesntExists() {
		Optional<Card> responseCard = cardRepository.findTop1ByCardNumber(this.card1.getCardNumber());
	 
	    Assert.assertFalse(responseCard.isPresent());
	}
	
}
