package br.com.ginezgit.issuer.authorization.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TRANSACTIONS")
public class Transaction implements Serializable {

	@EmbeddedId
	private TransactionId id;
	
//	@JoinColumn(foreignKey = @ForeignKey(name="none", value=ConstraintMode.NO_CONSTRAINT), nullable=true)
//	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@Column(name="card_number")
	private String cardNumber;
	
	private BigDecimal amount;
	
	@Column(name="auth_code")
	private String authorizationCode;
	
	@Column(name="resp_code")
	private TransactionResponseCode responseCode;
	
	private TransactionAction action;
	
}
