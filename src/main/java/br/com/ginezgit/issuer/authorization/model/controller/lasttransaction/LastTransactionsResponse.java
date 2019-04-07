package br.com.ginezgit.issuer.authorization.model.controller.lasttransaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class LastTransactionsResponse implements Serializable {

	
	@JsonProperty("cardnumber")
	private String cardNumber;
	
	@JsonProperty("availableAmount")
	private BigDecimal balance;
	
	@JsonProperty("transactions")
	private List<LastTransactionsResponseTransaction> transactions;
	
}
