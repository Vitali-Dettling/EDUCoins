package org.educoins.core.miner;

import java.io.Console;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

import org.educoins.core.client.Wallet;
import org.educoins.core.cryptography.SHA;

public class Miner {

	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 3;//TODO Change time to 30 which is equal to 5 min...
	private static final int MILLESECONDS = 1000;
	private static final long DESIRED_BLOCK_TIME = (long) DESIRED_TIME_PER_BLOCK_IN_SEC * CHECK_AFTER_BLOCKS * MILLESECONDS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final boolean RUNNING = true;
	private static final int BIT32 = 32;
	private static final String SHA256 = "SHA-256";
	
	private static int blockCounter;
	private static long lastBlockTime;
	

	public static void main(String[] args) throws IOException {

		//TODO: Tread ist nur drinnen, damit man den Rechner nutzen kann und er nicht voll beschäftig ist zu minern... 
		//Den Inhalt vom thread in den haupt thread einbinden, sobald der Miner seperat läuft...
		
		Wallet.enterWalletAddress();
		
		MinerThread minerThread = new MinerThread();
		minerThread.start();

		// Input inputTransacation = new Input();

		Thread.yield();

	}

	public static class MinerThread extends Thread {

		public void run() {

			// Hier Klasse um letzten Block aus der BlocChain, mit daten zu
			// bekommen...
			Block block = new Block();
			BlockChain.setAddress("./../../../BlockChain");
			
			lastBlockTime = System.currentTimeMillis();
			blockCounter = 0;
			
			while (true) {

				blockFound(block);
				BlockChain.newBlock(block);
				retargedDifficulty(block);
				miningPOW(block);
			}
		}
	}
	
	
	private static void blockFound(Block block){
		
		block.setnTx(block.getnTx()+1);
		//TODO Make seriouse though how it will work from here on forward????
		
		
	} 
	
	

	/**
	 * Bitcoin explanation: Mastering Bitcoin 195
	 * Every 2,016 blocks, all nodes retarget the proof-of-work difficulty. The
	 * equation for retargeting difficulty measures the time it took to find the
	 * last 2,016 blocks and compares that to the expected time of 20,160 minutes.
	 * 
	 * New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks / 20160 minutes)
	 * */
	private static Block retargedDifficulty(Block block) {
		
		//TODO: CHECK_AFTER_BLOCKS = 9 Ist nur zum Testen auf eine wirckliche Zahl umändern...
		if(blockCounter == CHECK_AFTER_BLOCKS){

			long currentBlockTime = System.currentTimeMillis();
			BigDecimal currentDifficulty = new BigDecimal(block.getDifficulty()).setScale(100, BigDecimal.ROUND_HALF_UP);;
			BigDecimal actualBlockTime = BigDecimal.valueOf(currentBlockTime - lastBlockTime).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);					
			BigDecimal newDifficulty = currentDifficulty.multiply(actualBlockTime.divide(BigDecimal.valueOf(DESIRED_BLOCK_TIME), BigDecimal.ROUND_HALF_DOWN).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP)); 
			
			block.setDifficulty(newDifficulty.toBigInteger());
			lastBlockTime = currentBlockTime;
			blockCounter = 0;
		}
		blockCounter++;
		
		return block;
	}

	/**
	 * Used this API's for converting purpose.
	 * https://commons.apache.org/proper/
	 * commons-lang/javadocs/api-release/index.html
	 * https://commons.apache.org/proper/commons-codec/apidocs/
	 * 
	 * https://litecoin.info/Block_hashing_algorithm
	 * https://litecoin.info/Scrypt
	 * https://github.com/wg/scrypt/blob/master/src/
	 * main/java/com/lambdaworks/crypto/SCrypt.java
	 * 
	 * */
	public static void miningPOW(Block block) {

		byte[] hashedHeader = getHashedHeader(block);
		byte[] random32Bit = new byte[BIT32];
		
		SecureRandom nonce = new SecureRandom();
		BigInteger target = block.getDifficulty();
		BigInteger challenge;

		while (RUNNING) {
			nonce.nextBytes(random32Bit);
			byte[] concatedByte = concat(hashedHeader, random32Bit);

			// For scrypt hashine!!! -> May change if possible???
			byte[] sha256Hashed = SHA.getMessageDigest(SHA256).digest(concatedByte);
			challenge = invertNegaitve(sha256Hashed);

			// Enable for test use only!!! -> Delete
			   System.err.println("Challenge: " + challenge.toString().length());
			   System.err.println("Target:    " + target.toString().length());

			if (compareBigInteger(challenge, target)) {
				block = createNewBlock(challenge, random32Bit, block.getDifficulty());
				break;
			}
		}
	}

	private static Block createNewBlock(BigInteger newHashValue, byte[] nonce, BigInteger newDifficulty) {
		
		Block newBlock = new Block();
		newBlock.setNewHashValue(newHashValue.toString());
		newBlock.setNonce(invertNegaitve(nonce));
		newBlock.setTimestamp(System.currentTimeMillis());
		newBlock.setDifficulty(newDifficulty);

		// TODO [Vitali] Müssen noch immplementiert werden und sich überlegen
		// wie???
		newBlock.setVersion(1);
		newBlock.setHashedMerkleRoot("0");
		// ///////////////////////////////////////////////////

		return newBlock;
	}

	private static BigInteger invertNegaitve(byte[] toInvertBitInteger) {
		boolean isNegative = (toInvertBitInteger[0] & 0x80) == 0x80;
		if (isNegative)
			toInvertBitInteger[0] &= 0x7f;
		return new BigInteger(toInvertBitInteger);
	}

	public static boolean compareBigInteger(BigInteger challenge, BigInteger target) {

		int res = challenge.compareTo(target);

		if (res == 0){
			return true;
		}// str1 = "Both values are equal ";
		else if (res == 1){
			return false;
		}// str2 = "First Value is greater ";
		else if (res == -1){
			return true;// str3 = "Second value is greater";
		}
		else{
			System.err.println("ERROR: Challange and Target comparrison did not work.");
			return false;
		}
		
	}

	private static byte[] getHashedHeader(Block lastblock) {

		byte[] version = byteToArray(lastblock.getVersion());
		byte[] hashPrevBlock = lastblock.getHashedPrevBlock().getBytes();
		byte[] hashMerkleRoot = lastblock.getHashedMerkleRoot().getBytes();
		byte[] timeStamp = byteToArray(lastblock.getTimestamp());
		byte[] difficulty = lastblock.getDifficulty().toByteArray();

		return concat(version, hashPrevBlock, hashMerkleRoot, timeStamp, difficulty);
	}

	private static byte[] byteToArray(final long i) {
		BigInteger bigInt = BigInteger.valueOf(i);
		return bigInt.toByteArray();
	}

	private static byte[] concat(byte[]... arrays) {
		// Determine the length of the result array
		int totalLength = 0;
		for (int i = 0; i < arrays.length; i++) {
			totalLength += arrays[i].length;
		}

		// create the result array
		byte[] result = new byte[totalLength];

		// copy the source arrays into the result array
		int currentIndex = 0;
		for (int i = 0; i < arrays.length; i++) {
			System.arraycopy(arrays[i], 0, result, currentIndex,
					arrays[i].length);
			currentIndex += arrays[i].length;
		}

		return result;
	}
	
	

	
	
	
	
	
	
	

}
