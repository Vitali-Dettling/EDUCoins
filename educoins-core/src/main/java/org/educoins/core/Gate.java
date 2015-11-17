package org.educoins.core;

import java.security.SecureRandom;

import org.educoins.core.utils.ByteArray;

import com.google.common.base.Objects;

public class Gate {

	private static final int randomNumberLength256 = 256;
	private static final int HEX = 16;
	
	private String signatures;
	private String publicKey;
	private String message;
	private String signHere;
	
	public Gate(String signature, String publicKey){
		
		this.signatures = signature;
		this.publicKey = publicKey;
		this.message = getSecureRandomString256HEX();	
	}
	
	private String getSecureRandomString256HEX(){
		SecureRandom secureRandom = new SecureRandom();
		byte[] nextByte = new byte[randomNumberLength256]; 
		secureRandom.nextBytes(nextByte);
		return ByteArray.convertToString(nextByte, HEX);
	}
	
	public String getSignature() {
	
		return this.signatures;
	}
	
	public void setSignature(String signature) {
		
		this.signatures = signature;
	}

	public String getPublicKey() {
		
		return this.publicKey;
	}
	
	public String getMessage(){
		
		return this.message;
	}
	
	public String externSignature(String signature){
		return this.signHere = signature;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Gate) {
			Gate that = (Gate) object;
			return Objects.equal(this.signatures, that.signatures) && Objects.equal(this.publicKey, that.publicKey)
					&& Objects.equal(this.message, that.message) && Objects.equal(this.signHere, that.signHere);
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("signatures", signatures)
				.add("publicKey", publicKey)
				.add("message", message)
				.add("signHere", signHere)
				.toString();
	}

	public byte[] getConcatedGate(){
		
		byte[] message = ByteArray.convertFromString(getMessage());
		byte[] publicKey = ByteArray.convertFromString(getPublicKey());
		
		return ByteArray.concatByteArrays(message, publicKey);	
	}


	
	
}
