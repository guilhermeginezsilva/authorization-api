package br.com.ginezgit.issuer.authorization.util;

import java.util.Optional;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class Instance {

	public static boolean isEmpty(Object instance) {
		if(!Optional.ofNullable(instance).isPresent()) {
			return true;
		}
		
		if (instance instanceof String) {
			return ((String) instance).isEmpty();
		}
		
		return false;
	}
	
}
