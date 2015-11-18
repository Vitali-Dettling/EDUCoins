package educoins.core.utils;

import java.security.SecureRandom;

import org.educoins.core.utils.Sha256Hash;

public class Generator {
	
	private static final int BYTE_256 = 256;
	
	private static SecureRandom secureRandom = new SecureRandom();
	
	public static String getSecureRandomString256HEX(){
		byte[] nextByte = new byte[BYTE_256]; 
		secureRandom.nextBytes(nextByte);
		return Sha256Hash.wrap(nextByte).toString();
	}
}