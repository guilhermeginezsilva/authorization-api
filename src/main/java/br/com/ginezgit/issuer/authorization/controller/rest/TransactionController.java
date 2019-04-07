
package br.com.ginezgit.issuer.authorization.controller.rest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.ginezgit.issuer.authorization.model.controller.lasttransaction.LastTransactionsResponse;
import br.com.ginezgit.issuer.authorization.service.TransactionService;

@CrossOrigin
@RestController
@RequestMapping("/v1/transactions")
@Validated
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@RequestMapping(value="/cards/{cardNumber}", method= RequestMethod.GET)
	public ResponseEntity<?> getLastTransactions(
			@NotBlank(message="Card number must not be null or blank")
			@Size(min=15, max=19, message="Invalid card number length")
			@Pattern(regexp="^[0-9]+", message="Card number must only contain numbers")
			@PathVariable("cardNumber")
			String cardNumber) {
		
			LastTransactionsResponse lastTransactionsResponse = transactionService.getLast10Transactions(cardNumber);
			return ResponseEntity.ok(lastTransactionsResponse);
	}
	
}
