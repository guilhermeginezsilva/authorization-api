package br.com.ginezgit.issuer.authorization.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ginezgit.issuer.authorization.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {

	Optional<Card> findTop1ByCardNumber(String cardNumber);

}
