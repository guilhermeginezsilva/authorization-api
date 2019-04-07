package br.com.ginezgit.issuer.authorization;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import br.com.ginezgit.issuer.authorization.model.Card;
import br.com.ginezgit.issuer.authorization.repository.CardRepository;

@Component
public class InitialData {

	@Autowired
	private CardRepository cardRepository;
	
	@EventListener(ApplicationReadyEvent.class)
	public void initializeData() {
		Card card1 = new Card("1234567890123456", new BigDecimal(1000), "Ginez Git");
		Card card2 = new Card("1234567890123457", new BigDecimal(1000), "Guest");
		Card card3 = new Card("1234567890123458", new BigDecimal(50000), "Rich card");
		
		Optional<Card> card1Db = cardRepository.findTop1ByCardNumber(card1.getCardNumber());
		if(card1Db.isPresent()) {
//			card1Db.get().setBalance(new BigDecimal(1000));
//			cardRepository.save(card1Db.get());
		} else {
			cardRepository.save(card1);
		}
		
		Optional<Card> card2Db = cardRepository.findTop1ByCardNumber(card2.getCardNumber());
		if(card2Db.isPresent()) {
//			card2Db.get().setBalance(new BigDecimal(1000));
//			cardRepository.save(card2Db.get());
		} else {
			cardRepository.save(card2);
		}
		
		Optional<Card> card3Db = cardRepository.findTop1ByCardNumber(card3.getCardNumber());
		if(card3Db.isPresent()) {
//			card3Db.get().setBalance(new BigDecimal(1000));
//			cardRepository.save(card3Db.get());
		} else {
			cardRepository.save(card3);
		}
	}
	
}
