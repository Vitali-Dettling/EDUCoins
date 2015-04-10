package org.educoins.core.miner;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.educoins.core.cryptography.Scrypt;




public final class PoW {
	
	
	private final static Block blockHeader;
	
	
	public PoW(){
		
		Block blockHeader = new Block();
		
		
		findPoW();
	}
	
	
	
	/*
	 * Information:
	 * https://litecoin.info/Block_hashing_algorithm
	 * https://litecoin.info/Scrypt
	 * https://github.com/wg/scrypt/blob/master/src/main/java/com/lambdaworks/crypto/SCrypt.java
	 * */
	private static void findPoW(){
	

		byte[] challenge = getChallenge();
		answer = RandomNumberGenerator(ascii .UpperCase() +ASCII.LowerCase() + Digits());
		
		
		SecureRandom random = new SecureRandom();
	    byte bytes[] = new byte[20];
	    random.nextBytes(bytes);
		
		
			
	}
	

    private static final ThreadLocal<SecureRandom> secureRandom = new ThreadLocal<SecureRandom>() {
        @Override
        protected SecureRandom initialValue() {
            return new SecureRandom();
        }
    };
	
	private static byte[] getChallenge(){
		
		Scrypt scrypt = new Scrypt();
		
		byte[] version = byteToArray(blockHeader.getVersion());
		byte[] hashPrevBlock = blockHeader.getHashedPrevBlock().getBytes();
		byte[] hashMerkleRoot = blockHeader.getHashedMerkleRoot().getBytes();
		byte[] timeStamp = byteToArray(blockHeader.getTimeStamp());
		byte[] difficulty = byteToArray(blockHeader.getTimeStamp());
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(version);
		outputStream.write(hashPrevBlock);
		outputStream.write(hashMerkleRoot);
		outputStream.write(timeStamp);
		outputStream.write(difficulty);
		
		return scrypt.hash(outputStream.toByteArray()); 
	}
	
	
	
	private static byte[] byteToArray( final int i ) {
	    BigInteger bigInt = BigInteger.valueOf(i);      
	    return bigInt.toByteArray();
	}
		


	
	
	
	
	
	
	
	
	
	

}
