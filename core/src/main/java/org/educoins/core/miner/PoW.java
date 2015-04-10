package org.educoins.core.miner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.educoins.core.cryptography.Scrypt;


public class PoW {
	
	
	private final static int running = 0;
	private final static int exit = 1;
	private final static int bit32 = 32;
	private final static int eightHexaDecimalDigits = 4;
	
	private final Scrypt scrypt;
	private Block blockHeader;
	
	
	public PoW(){
		
		this.blockHeader = new Block();
		this.scrypt = new Scrypt();
		
		findPoW();
	}
	
	/*
	 * Information:
	 * https://litecoin.info/Block_hashing_algorithm
	 * https://litecoin.info/Scrypt
	 * https://github.com/wg/scrypt/blob/master/src/main/java/com/lambdaworks/crypto/SCrypt.java
	 * */
	private void findPoW(){
	
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		byte[] hashedHeader = getHashedHeader(outputStream);					   
		SecureRandom nonce = new SecureRandom();
		byte[] random32Bit = new byte[bit32];
		
		int target = getTarget();
		int challenge;
		
		int loop = running;
		while(loop == 0)
		{
			
			try {
				
				outputStream.write(hashedHeader);			
				outputStream.write(random32Bit);
				
				System.out.println("Random Generator: Check whether it is realy random:" + nonce.toString());//Löschen nach testen!!!
		
				outputStream.flush();
				nonce.nextBytes(random32Bit);
				
				challenge = Integer.parseInt(this.scrypt.hash(outputStream.toByteArray()).toString());
	
				if(challenge < target ){
					loop = exit;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(e.getMessage() + "Class: PoW");
			}
		}	
	}
	
	
	/*
	 * Information:
	 * http://bitcoin.stackexchange.com/questions/2924/how-to-calculate-new-bits-value
	 * https://bitcoin.org/en/developer-reference#merkle-trees
	 * */
	private int getTarget(){
		
		//Formula: target = coefficient * 2^(8 * (exponent – 3))
		//the first two hexadecimal digits => exponent 
		//the next six hex digits => coefficient
		
		//????????????????????????????????????????? if that work ?????????????????????????????????????????????
		byte[] hexTarget = ByteBuffer.allocate(eightHexaDecimalDigits).putInt(blockHeader.getDifficulty()).array();
		
		int coefficient = Integer.parseInt(ByteBuffer.wrap(hexTarget, 1, 4).toString());//Da muss es doch eine bessere MEthode geben um es zu lösen??? -> Als anstadt hin und her zu casten...
		int exponent = Integer.parseInt(ByteBuffer.wrap(hexTarget, 0, 1).toString());//Da muss es doch eine bessere MEthode geben um es zu lösen??? -> Als anstadt hin und her zu casten...
		
		return coefficient * 2 ^(8 * (exponent - 3));
	}
	
    private final ThreadLocal<SecureRandom> secureRandom = new ThreadLocal<SecureRandom>() {
        @Override
        protected SecureRandom initialValue() {
            return new SecureRandom();
        }
    };
    
	
	private byte[] getHashedHeader(ByteArrayOutputStream outputStream){
		
		try {
		
		byte[] version = byteToArray(blockHeader.getVersion());
		byte[] hashPrevBlock = blockHeader.getHashedPrevBlock().getBytes();
		byte[] hashMerkleRoot = blockHeader.getHashedMerkleRoot().getBytes();
		byte[] timeStamp = byteToArray(blockHeader.getTimeStamp());
		byte[] difficulty = byteToArray(blockHeader.getDifficulty());
			
		outputStream.write(version);
		outputStream.write(hashPrevBlock);
		outputStream.write(hashMerkleRoot);
		outputStream.write(timeStamp);
		outputStream.write(difficulty);
		
		return scrypt.hash(outputStream.toByteArray()); 
		
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage() +  "-> Class: PoW");
			return null;
		}
	}
	
	
	
	private byte[] byteToArray( final int i ) {
	    BigInteger bigInt = BigInteger.valueOf(i);      
	    return bigInt.toByteArray();
	}
		


	
	
	
	
	
	
	
	
	
	

}
