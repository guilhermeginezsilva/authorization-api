package br.com.ginezgit.issuer.authorization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ginezgit.issuer.authorization.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findTop10ByCardNumberOrderByIdNsuDesc(String cardNumber);

	Optional<Transaction> findTopByAuthorizationCodeNotNullOrderByAuthorizationCodeDesc();

	Optional<Transaction> findTopById_NsuNotNullOrderById_NsuDesc();
	
}
