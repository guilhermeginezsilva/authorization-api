package br.com.ginezgit.issuer.authorization.util;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.ginezgit.issuer.authorization.model.Transaction;
import br.com.ginezgit.issuer.authorization.repository.TransactionRepository;

@Component
@Scope("application")
public class UniqueCodeGenerator {

	@Autowired
	private TransactionRepository transactionRepository;
	
	private Integer currentAuthCodeId;
	private Integer currentNsuId;
	
	@PostConstruct
	public void init() {
		getLastAuthCodeFromDatabase();
		getLastNsuFromDatabase();
	}
	
	private void getLastAuthCodeFromDatabase() {
		Optional<Transaction> lastTransactionOptional = transactionRepository.findTopByAuthorizationCodeNotNullOrderByAuthorizationCodeDesc();
		if(lastTransactionOptional.isPresent()) {
			this.currentAuthCodeId = Integer.parseInt(lastTransactionOptional.get().getAuthorizationCode())+1;
		} else {
			this.currentAuthCodeId = 0;
		}
	}
	
	private void getLastNsuFromDatabase() {
		Optional<Transaction> lastTransactionOptional = transactionRepository.findTopById_NsuNotNullOrderById_NsuDesc();
		if(lastTransactionOptional.isPresent()) {
			this.currentNsuId = lastTransactionOptional.get().getId().getNsu()+1;
		} else {
			this.currentNsuId = 0;
		}
	}
	
	public String generateAuthorizationCode() {
		synchronized (currentAuthCodeId) {
			return String.format("%06d" , currentAuthCodeId++);
		}
	}
	
	public Integer generateNsu() {
		synchronized (currentNsuId) {
			return currentNsuId++;
		}
	}
	
	
}
