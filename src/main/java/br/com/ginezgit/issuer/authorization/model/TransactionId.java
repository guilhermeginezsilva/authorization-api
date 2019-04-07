package br.com.ginezgit.issuer.authorization.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TransactionId implements Serializable {
	@GeneratedValue(strategy=GenerationType.AUTO) 
	public Integer nsu;
	public Date date;
}
