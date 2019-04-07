package br.com.ginezgit.issuer.authorization.model.controller.lasttransaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.ginezgit.issuer.authorization.model.TransactionResponseCode;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class LastTransactionsResponseTransaction implements Serializable {

	@JsonProperty("date")
	private Date date;
	
	@JsonProperty("amount")
	private BigDecimal amount;
	
	@JsonProperty("responseCode")
	private TransactionResponseCode responseCode;
	
}
