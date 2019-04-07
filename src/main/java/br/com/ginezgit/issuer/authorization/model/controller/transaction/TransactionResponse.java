package br.com.ginezgit.issuer.authorization.model.controller.transaction;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.com.ginezgit.issuer.authorization.model.TransactionAction;
import br.com.ginezgit.issuer.authorization.model.TransactionResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class TransactionResponse implements Serializable {

	@JsonProperty("action")
	private TransactionAction action;
	@JsonProperty("code")
	private TransactionResponseCode responseCode;
	@JsonProperty("authorization_code")
	private String authorizationCode;
	
}
