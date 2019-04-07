package br.com.ginezgit.issuer.authorization.model;

import java.io.Serializable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionAction implements Serializable {
	WITHDRAW('W', "withdraw");
	
	private Character actionCode;
	private String jsonValue;
	
	private TransactionAction(Character actionCode, String jsonValue) {
		this.actionCode = actionCode;
		this.jsonValue = jsonValue;
	}

	public Character getActionCode() {
		return actionCode;
	}

	@JsonValue
	public String getJsonValue() {
		return jsonValue;
	}

	@Converter(autoApply=true)
	public static class TransactionActionDbConverter implements AttributeConverter<TransactionAction, Character> {

	    @Override
	    public Character convertToDatabaseColumn(TransactionAction action) {
	        switch (action) {
	            case WITHDRAW:
	                return 'W';
	            default:
	                throw new IllegalArgumentException("Unknown" + action);
	        }
	    }

	    @Override
	    public TransactionAction convertToEntityAttribute(Character actionChar) {
	        switch (actionChar) {
	            case 'W':
	                return WITHDRAW;
	            default:
	                throw new IllegalArgumentException("Unknown" + actionChar);
	        }
	    }
	}
	
}


