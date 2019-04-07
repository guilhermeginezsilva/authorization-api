package br.com.ginezgit.issuer.authorization.model;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionResponseCode implements Serializable {
	APROVED("00"),
	INSUFICIENT_FUNDS("51"),
	INVALID_ACCOUNT("14"),
	PROCESSMENT_ERROR("96");
	
	private String responseCode;
	
	private TransactionResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@JsonValue
	public String getCode() {
		return responseCode;
	}


	@Converter(autoApply=true)
	public static class TransactionResponseCodeDbConverter implements AttributeConverter<TransactionResponseCode, String> {

	    @Override
	    public String convertToDatabaseColumn(TransactionResponseCode action) {
	        switch (action) {
	            case APROVED:
	                return "00";
	            case INSUFICIENT_FUNDS:
	                return "51";
	            case INVALID_ACCOUNT:
	                return "14";
	            case PROCESSMENT_ERROR:
	                return "96";
	            default:
	                throw new IllegalArgumentException("Unknown" + action);
	        }
	    }

	    @Override
	    public TransactionResponseCode convertToEntityAttribute(String responseCode) {
	        switch (responseCode) {
	            case "00":
	                return APROVED;
	            case "51":
	                return INSUFICIENT_FUNDS;
	            case "14":
	                return INVALID_ACCOUNT;
	            case "96":
	                return PROCESSMENT_ERROR;
	            default:
	                throw new IllegalArgumentException("Unknown" + responseCode);
	        }
	    }
	}
	
}


