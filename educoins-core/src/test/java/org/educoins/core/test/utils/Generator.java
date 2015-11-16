package org.educoins.core.test.utils;

import java.security.SecureRandom;

import org.educoins.core.utils.ByteArray;

public class Generator {
	
	private static final int randomNumberLength256 = 256;
	private static final int HEX = 16;
	
	private SecureRandom secureRandom;
	
	public Generator(){
		 this.secureRandom = new SecureRandom();
	}
	
	public String getSecureRandomString256HEX(){
		byte[] nextByte = new byte[randomNumberLength256]; 
		this.secureRandom.nextBytes(nextByte);
		return ByteArray.convertToString(nextByte, HEX);
	}
	
	public byte[] getSecureRandomByteArray256(){
		byte[] nextByte = new byte[randomNumberLength256]; 
		this.secureRandom.nextBytes(nextByte);
		return nextByte;
	}

}
