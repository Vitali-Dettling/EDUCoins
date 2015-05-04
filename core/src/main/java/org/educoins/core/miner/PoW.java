package org.educoins.core.miner;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;

import org.apache.commons.codec.binary.StringUtils;
import org.educoins.core.cryptography.SHA;




public class PoW {
		
	private final static boolean RUNNING = true;
	private final static int BIT32 = 32;
	private final static String SHA256 = "SHA-256";
	
	/**
	 * Used this API's for converting purpose.
	 * https://commons.apache.org/proper/commons-lang/javadocs/api-release/index.html
	 * https://commons.apache.org/proper/commons-codec/apidocs/
	 * 
	 * https://litecoin.info/Block_hashing_algorithm
	 * https://litecoin.info/Scrypt
	 * https://github.com/wg/scrypt/blob/master/src/main/java/com/lambdaworks/crypto/SCrypt.java
	 * 
	 * */
public Block startMiningPOW(Block lastBlock){
	
		byte[] hashedHeader = getHashedHeader(lastBlock);
		byte[] random32Bit = new byte[BIT32];
		byte[] targetInByteArray  = StringUtils.getBytesUsAscii(lastBlock.getDifficulty());
		
		SecureRandom nonce = new SecureRandom();
		BigInteger target = new BigInteger(targetInByteArray);
		BigInteger challenge;

		while(RUNNING)
		{
			nonce.nextBytes(random32Bit);			
			byte[] concatedByte = concat(hashedHeader , random32Bit);
			
			//For scrypt hashine!!! -> May change if possible???
			byte[] sha256Hashed = SHA.getMessageDigest(SHA256).digest(concatedByte);
			challenge = invertNegaitve(sha256Hashed);

			//Enable for test use only!!! -> Delete
			//System.err.println("Challenge: " + challenge.toString());
			//System.err.println("Target:    " + target.toString());

			if(compareBigInteger(challenge, target)){	
				return SetNewBlock(challenge, random32Bit);
			}	
		}	
	}

	private Block SetNewBlock(BigInteger newHashValue, byte[] nonce)
	{
		Block newBlock = new Block();
		newBlock.setNewHashValue(newHashValue.toString());
		newBlock.setNonce(invertNegaitve(nonce));
		newBlock.setTimestamp(System.currentTimeMillis());
		
		//todo: Müssen noch immplementiert werden und sich überlegen wie??? 
		newBlock.setVersion(1);
		newBlock.setHashedMerkleRoot("0");
		/////////////////////////////////////////////////////77
		
		return newBlock;
	}
	

	private BigInteger invertNegaitve(byte[] toInvertBitInteger){
		boolean isNegative = (toInvertBitInteger[0] & 0x80) == 0x80;
		if (isNegative)
			toInvertBitInteger[0] &= 0x7f;
		return new BigInteger(toInvertBitInteger);
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
	
 
	private byte[] getHashedHeader(Block lastblock){
		
		byte[] version = byteToArray(lastblock.getVersion());
		byte[] hashPrevBlock = lastblock.getHashedPrevBlock().getBytes();
		byte[] hashMerkleRoot = lastblock.getHashedMerkleRoot().getBytes();
		byte[] timeStamp = byteToArray(lastblock.getTimestamp());
		byte[] difficulty = StringUtils.getBytesUsAscii(lastblock.getDifficulty());
		
		return concat(version, hashPrevBlock, hashMerkleRoot, timeStamp, difficulty);
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
