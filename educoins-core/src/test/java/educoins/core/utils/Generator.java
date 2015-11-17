package educoins.core.utils;

import java.security.SecureRandom;

import org.educoins.core.utils.ByteArray;

public class Generator {
	
	private static final int randomNumberLength256 = 256;
	private static final int HEX = 16;
	
	private static SecureRandom secureRandom = new SecureRandom();
	
	public static String getSecureRandomString256HEX(){
		byte[] nextByte = new byte[randomNumberLength256]; 
		secureRandom.nextBytes(nextByte);
		return ByteArray.convertToString(nextByte, HEX);
	}
	
	public static byte[] getSecureRandomByteArray256(){
		byte[] nextByte = new byte[randomNumberLength256]; 
		secureRandom.nextBytes(nextByte);
		return nextByte;
	}

}
