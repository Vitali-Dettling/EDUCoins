package org.educoins.core.cryptography;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SHA {
	
	
	public static MessageDigest getMessageDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
          //  Logger.logMessage("Missing message digest algorithm: " + algorithm); 
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static MessageDigest sha256() {
        return getMessageDigest("SHA-256");
    }
	
	

}
