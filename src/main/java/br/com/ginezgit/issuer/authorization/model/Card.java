package br.com.ginezgit.issuer.authorization.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

	@Id
	private String cardNumber;
	
//	@OneToMany(cascade=CascadeType.REFRESH, mappedBy="card")
//	private List<Transaction> transactions;
	
	private BigDecimal balance;
	
	private String customerName;
	
}
