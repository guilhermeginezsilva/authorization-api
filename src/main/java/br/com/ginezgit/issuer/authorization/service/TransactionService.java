package br.com.ginezgit.issuer.authorization.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import br.com.ginezgit.issuer.authorization.exception.CardNotFoundException;
import br.com.ginezgit.issuer.authorization.exception.InsuficientFundsException;
import br.com.ginezgit.issuer.authorization.exception.InvalidRequestParameters;
import br.com.ginezgit.issuer.authorization.exception.TransactionPersistanceException;
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
import br.com.ginezgit.issuer.authorization.util.Instance;
import br.com.ginezgit.issuer.authorization.util.UniqueCodeGenerator;

@Service
public class TransactionService {

	Logger logger = LoggerFactory.getLogger(TransactionService.class);
	
	@Autowired
	private TransactionRepository transactionRespository;
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private UniqueCodeGenerator authorizationCodeGenerator;
	
	public LastTransactionsResponse getLast10Transactions(String cardNumber) {
		
		List<Transaction> transactions = transactionRespository.findTop10ByCardNumberOrderByIdNsuDesc(cardNumber);
		Optional<Card> cardOptional = cardRepository.findTop1ByCardNumber(cardNumber);
		
		Card card = cardOptional.orElseThrow(() -> new CardNotFoundException("Card number couldn't be found"));
		
		LastTransactionsResponse response = new LastTransactionsResponse(
				cardNumber, 
				card.getBalance(),
				transactions.stream().map(dbTransaction -> 
				new LastTransactionsResponseTransaction(dbTransaction.getId().getDate(), dbTransaction.getAmount(), dbTransaction.getResponseCode()))
				.collect(Collectors.toList())
		);
		return response;
	}

	@Transactional
	public TransactionResponse withdraw(TransactionRequest transactionRequest) {
		Transaction transaction;
		try {
			validateRequest(transactionRequest);
			
			Optional<Card> cardOptional = cardRepository.findTop1ByCardNumber(transactionRequest.getCardNumber());
			cardOptional.orElseThrow(() -> new CardNotFoundException("Card number not found"));
			
			Card card = cardOptional.get();
			checkAndUpdateCardBalance(card, transactionRequest.getAmount());
			transaction = saveAprovedTransaction(transactionRequest, card);
		} catch (InvalidRequestParameters e) {
			transaction = saveDeniedTransaction(transactionRequest, TransactionResponseCode.PROCESSMENT_ERROR);
			logger.debug("Transaction was denied due to invalid account - NSU["+transaction.getId().getNsu()+"]", e);
		} catch (CardNotFoundException e) {
			transaction = saveDeniedTransaction(transactionRequest, TransactionResponseCode.INVALID_ACCOUNT);
			logger.debug("Transaction was denied due to invalid account - NSU["+transaction.getId().getNsu()+"]", e);
		} catch (InsuficientFundsException e) {
			transaction = saveDeniedTransaction(transactionRequest, TransactionResponseCode.INSUFICIENT_FUNDS);
			logger.debug("Transaction was denied due to insuficient funds - NSU["+transaction.getId().getNsu()+"]", e);
		} catch (Exception e) {
			transaction = saveDeniedTransaction(transactionRequest, TransactionResponseCode.PROCESSMENT_ERROR);
			logger.error("Transaction was denied due to processment error - NSU["+transaction.getId().getNsu()+"]", e);
		}
		
		if(Instance.isEmpty(transaction)) {
			String message = "An critical error has ocurred when trying to save transaction to database";
			logger.error(message);
			throw new TransactionPersistanceException(message);
		}
		
		return new TransactionResponse(TransactionAction.WITHDRAW, transaction.getResponseCode(), transaction.getAuthorizationCode());
	}

	private void validateRequest(TransactionRequest transactionRequest) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(transactionRequest);
		
		if(violations.size() > 0) {
			throw new InvalidRequestParameters();
		}
	}

	private Transaction saveAprovedTransaction(TransactionRequest transactionRequest, Card card) {
		Transaction transaction = new Transaction(
				new TransactionId(authorizationCodeGenerator.generateNsu(), new Date()),
				card.getCardNumber(),
				transactionRequest.getAmount(),
				authorizationCodeGenerator.generateAuthorizationCode(),
				TransactionResponseCode.APROVED,
				transactionRequest.getAction()
			);

			cardRepository.save(card);
			return transactionRespository.save(transaction);
	}
	
	private Transaction saveDeniedTransaction(TransactionRequest transactionRequest, TransactionResponseCode responseCode) {
		Transaction transaction = new Transaction(
				new TransactionId(authorizationCodeGenerator.generateNsu(), new Date()),
				transactionRequest.getCardNumber(),
				transactionRequest.getAmount(),
				null,
				responseCode,
				transactionRequest.getAction()
			);

			transactionRespository.save(transaction);
			return transaction;
	}
	
	private void checkAndUpdateCardBalance(Card card, BigDecimal amount) {
		if(card.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) == -1) {
			throw new InsuficientFundsException();
		}
		
		card.setBalance(card.getBalance().subtract(amount));
	}

}
