package org.educoins.core.miner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.educoins.core.cryptography.Hashes;
import org.educoins.core.cryptography.SHA;
import org.educoins.core.cryptography.Scrypt;




public class PoW {
	
	
	private final static int RUNNING = 0;
	private final static int EXIT = 1;
	private final static int BIT32 = 32;
	private final static int BIT256 = 256;
	private final static int HEX = 16;
	
	private final Scrypt scrypt;
	private Block blockHeader;
	
	private byte[] test;
		
	
	public PoW(Block block){
		
		this.blockHeader = block;
		this.scrypt = new Scrypt();
		

		
		findPoW();
	}
	
	/**
	 * Information:
	 * https://litecoin.info/Block_hashing_algorithm
	 * https://litecoin.info/Scrypt
	 * https://github.com/wg/scrypt/blob/master/src/main/java/com/lambdaworks/crypto/SCrypt.java
	 * */
	private void findPoW(){
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		byte[] hashedHeader = getHashedHeader(outputStream);
		
		
		this.test = hashedHeader;
		
		SecureRandom nonce = new SecureRandom();
		byte[] random32Bit = new byte[BIT32];
		
		BigInteger target = getDifficultyTargetAsInteger();
		BigInteger challenge;
		
		int loop = RUNNING;
		while(loop == RUNNING)
		{
				nonce.nextBytes(random32Bit);			
				byte[] concatByte = concat(hashedHeader , random32Bit);
				
//Enable for test use only!!! -> Delte
//				System.out.println("hashedHeader Original: " + Arrays.toString(hashedHeader));
//				System.out.println("               Random: " + Arrays.toString(random32Bit));
//				System.out.println("hashedHeader + Random: " + Arrays.toString(concatByte));
				
		
			byte[] scryptHashed = SHA.sha256().digest(concatByte);
//For scrypt hashine!!! -> May change if possible???
//			byte[] scryptHashed = this.scrypt.hash(outputStream.toByteArray()); -> Hash with scrypt...
			
			long convertedByteArrayIntoLong =  convertIntoLong(scryptHashed);
			
			challenge = Utils.decodeCompactBits(convertedByteArrayIntoLong);
			System.err.println("challenge: " + challenge.toString());
			
			if(compareBigInteger(challenge , target)){
				loop = EXIT;
			}	
		}	
	}
	
	private long convertIntoLong(byte[] byteArrayToConvert){
		long converted = 0;
		for (int i = 0; i < byteArrayToConvert.length; i++)
		{
			converted = (converted << 8) + (byteArrayToConvert[i] & 0xff);
		}
		return converted;
	}
	
	
	public boolean compareBigInteger(BigInteger challenge, BigInteger target) {

        int res = challenge.compareTo(target);

		if( res == 0 )
		return true;// str1 = "Both values are equal ";
		else if( res == 1 )
		return false;// str2 = "First Value is greater ";
		else if( res == -1 )
		return true;// str3 = "Second value is greater";
		else
		System.err.println("ERROR: Challange and Target comparrison did not work.");
		return false;
    }
	
    /**
     * Returns the difficulty target as a 256 bit value that can be compared to a SHA-256 hash. Inside a block the
     * target is represented using a compact form. If this form decodes to a value that is out of bounds, an exception
     * is thrown.
     */
    public BigInteger getDifficultyTargetAsInteger(){
       
        BigInteger target = Utils.decodeCompactBits(this.blockHeader.getDifficulty());
        if (target.compareTo(BigInteger.ZERO) <= 0)
            System.err.println("Target: " + target.toString());
        return target;
    }
 
	private byte[] getHashedHeader(ByteArrayOutputStream outputStream){
		
		byte[] version = byteToArray(blockHeader.getVersion());
		byte[] hashPrevBlock = blockHeader.getHashedPrevBlock().getBytes();
		byte[] hashMerkleRoot = blockHeader.getHashedMerkleRoot().getBytes();
		byte[] timeStamp = byteToArray(blockHeader.getTimestamp());//!!! Cast to int -> Wrong!!!
		byte[] difficulty = byteToArray(blockHeader.getDifficulty());
		
		byte[] concatByte = concat(version, hashPrevBlock, hashMerkleRoot, timeStamp, difficulty);
			
//For scrypt hashine!!! -> May change if possible???		
//		return scrypt.hash(outputStream.toByteArray()); 
		return SHA.sha256().digest(concatByte);			
	}
	
	private byte[] byteToArray( final long i ) {
	    BigInteger bigInt = BigInteger.valueOf(i);      
	    return bigInt.toByteArray();
	}		

	private byte[] concat(byte[]...arrays)
	{
	    // Determine the length of the result array
	    int totalLength = 0;
	    for (int i = 0; i < arrays.length; i++)
	    {
	        totalLength += arrays[i].length;
	    }

	    // create the result array
	    byte[] result = new byte[totalLength];

	    // copy the source arrays into the result array
	    int currentIndex = 0;
	    for (int i = 0; i < arrays.length; i++)
	    {
	        System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
	        currentIndex += arrays[i].length;
	    }

	    return result;
	}
	
	
	
	

}
