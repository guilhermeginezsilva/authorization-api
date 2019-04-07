//package br.com.ginezgit.issuer.authorization.model;
//
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Account {
//
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//	public Long id;
//	
//	public String customerName;
//	
//	@OneToMany(mappedBy="account", cascade=CascadeType.REFRESH)
//	public List<Card> cards;
//	
//}
