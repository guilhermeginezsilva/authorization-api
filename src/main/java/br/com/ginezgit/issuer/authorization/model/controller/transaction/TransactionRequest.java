package br.com.ginezgit.issuer.authorization.model.controller.transaction;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.ginezgit.issuer.authorization.model.TransactionAction;
import br.com.ginezgit.issuer.authorization.util.Instance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class TransactionRequest implements Serializable {

	@NotNull(message="Action is mandatory")
	@JsonProperty("action")
	private TransactionAction action;
	
	@NotBlank(message="Card number must not be null or blank")
	@Size(min=15, max=19, message="Invalid card number length")
	@Pattern(regexp="^[0-9]+", message="Card number must only contain numbers")
	@JsonProperty("cardnumber")
	private String cardNumber;
	
	@NotNull(message="Card number must not be null or blank")
	@Min(value=0, message="Amount must be positive")
	@JsonProperty("amount")
	@JsonSerialize(using = MoneySerializer.class)
	@JsonDeserialize(using = MoneyDeserializer.class)
	private BigDecimal amount;
	
	public static class MoneySerializer extends JsonSerializer<BigDecimal> {
		
		public MoneySerializer() {
		}
		
	    @Override
	    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
	            JsonProcessingException {
	        jgen.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
	    }
	}
	
	public static class MoneyDeserializer extends JsonDeserializer<BigDecimal> {

		
		public MoneyDeserializer() {
			super();
		}

		@Override
		public BigDecimal deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			ObjectCodec oc = jp.getCodec();
			JsonNode node = oc.readTree(jp);
			String amountStr =  node.asText();
			if(Instance.isEmpty(amountStr)) {
				return null;
			}
			amountStr = amountStr.replaceAll(",",".");;
		    return new BigDecimal(amountStr);
		}
	}
}
