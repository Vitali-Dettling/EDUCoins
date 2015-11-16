package educoins.core.utils;

import org.educoins.core.utils.ByteArray;

import java.security.SecureRandom;

public class Generator {
	
	private static final int randomNumberLength256 = 256;
	private static final int HEX = 16;
	
	private SecureRandom secureRandom;
	
	public Generator(){
		 this.secureRandom = new SecureRandom();
	}
	
	public String getSecureRandomString256HEX(){
		byte[] nextByte = new byte[randomNumberLength256]; 
		return ByteArray.convertToString(nextByte, HEX);
	}
	
	public byte[] getSecureRandomByteArray256(){
		byte[] nextByte = new byte[randomNumberLength256]; 
		this.secureRandom.nextBytes(nextByte);
		return nextByte;
	}

}
