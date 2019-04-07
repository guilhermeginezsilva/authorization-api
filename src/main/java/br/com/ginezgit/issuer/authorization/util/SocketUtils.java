package br.com.ginezgit.issuer.authorization.util;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ginezgit.issuer.authorization.exception.AuthorizationHandlerException;

public class SocketUtils {
	
	private static Logger logger = LoggerFactory.getLogger(SocketUtils.class);
	
	private static final int DEFAULT_MESSAGE_LENGTH_FIELD_LENGTH = 4;

	public static Optional<String> receive(Socket connection) throws IOException {
		byte[] messageLengthBytes = new byte[DEFAULT_MESSAGE_LENGTH_FIELD_LENGTH];
		connection.getInputStream().read(messageLengthBytes, 0, DEFAULT_MESSAGE_LENGTH_FIELD_LENGTH);
		
		String messageLengthStr = new String(messageLengthBytes, StandardCharsets.UTF_8);
		if(!messageLengthStr.matches("^[0-9]+")) {
			connection.getInputStream().skip(connection.getInputStream().available());
			return Optional.empty();
		}
		int messageLengthInt = Integer.parseInt(messageLengthStr);
		
		byte[] messageContentBytes = new byte[messageLengthInt];
		connection.getInputStream().read(messageContentBytes, 0, messageLengthInt);
		
		String messageContentStrHex = new String(messageContentBytes, StandardCharsets.UTF_8);
		return Optional.ofNullable(StringHex.hexToStr(messageContentStrHex));
	}
	
	public static void send(Socket connection, String content) throws IOException {
		String messageContentStrHex = StringHex.strToHex(content);
		String paddedMessageLengthStr = String.format("%04d" , messageContentStrHex.length());
		
		String message = paddedMessageLengthStr+messageContentStrHex;

		connection.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
	}
	
	
	
}
