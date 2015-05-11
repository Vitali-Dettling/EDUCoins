package org.educoins.core.miner;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.client.Wallet;
import org.educoins.core.cryptography.ECDSA;
import org.educoins.core.cryptography.SHA;

import Transactions.EType;
import Transactions.Output;
import Transactions.Transaction;

public class Miner {

	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 3;//TODO Change time to 30 which is equal to 5 min...
	private static final int MILLESECONDS = 1000;
	private static final long DESIRED_BLOCK_TIME = (long) DESIRED_TIME_PER_BLOCK_IN_SEC * CHECK_AFTER_BLOCKS * MILLESECONDS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final boolean RUNNING = true;
	private static final int BIT32 = 32;
	private static final String SHA256 = "SHA-256";
	private static final int BLOCK_REWARD = 10; //TODO Dummy has to be changed...
	private static final int BIG_ENDIAN_ALWAYS_POSITIVE = 1;
	private static final int HEX = 16;
	
	//TODO Delete after Testing!!!
	private static final long testVersion = 1;
	///////////////////////////////////
	
	private static int blockCounter;
	private static long lastBlockTime;
	

	public static void main(String[] args) throws IOException, SignatureException {//TODO Delete Exception...

		//TODO: Tread ist nur drinnen, damit man den Rechner nutzen kann und er nicht voll beschäftig ist zu minern... 
		//Den Inhalt vom thread in den haupt thread einbinden, sobald der Miner seperat läuft...
		
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
			
			ECDSA publicKey = new ECDSA(); 
			//TODO Falsch, es darf keine direkte Verbindung zur Wallet gehen...
			Wallet.enterWalletAddress(publicKey.getPublicKey().toString());
			
			lastBlockTime = System.currentTimeMillis();
			blockCounter = 0;
			
			while (true) {

				BlockChain.newBlock(block);
				retargedDifficulty(block);
				block = miningPOW(block);
				reward(block);
			}
		}
	}
	
	
	public static void reward(Block block) {
		
		//TODO lockingScript procedure has to be established, which fits our needs...
		String lockingScript = EScripts.DUB.toString() + " " +
							   EScripts.HASH160.toString() + " " +
							   Wallet.getWalletAddress() + " " +//TODO Might needs to be delete after real address entry!!!
							   EScripts.EQUALVERIFY.toString() + " " +
							   EScripts.CHECKSIG.toString();
		//Input is empty because it is a coinbase transaction.
		List<Input> input = new ArrayList<Input>();
		List<Output> output = new ArrayList<Output>();
		output.add(new Output(BLOCK_REWARD, Wallet.getWalletAddress(), EType.COINBASE, lockingScript));
		//Input is empty because it is a coinbase transaction.
		Transaction transation = new Transaction(testVersion, input, output);
		block.addTransaction(transation);
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
			BigDecimal currentDifficulty = new BigDecimal(new BigInteger(block.getDifficulty(), HEX)).setScale(100, BigDecimal.ROUND_HALF_UP);
			BigDecimal actualBlockTime = BigDecimal.valueOf(currentBlockTime - lastBlockTime).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);	
			// New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks / 20160 minutes)
			BigDecimal newDifficulty = currentDifficulty.multiply(actualBlockTime.divide(BigDecimal.valueOf(DESIRED_BLOCK_TIME), BigDecimal.ROUND_HALF_DOWN).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP)); 
			block.setDifficulty(newDifficulty.toBigInteger().toString(HEX));			
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
	public static Block miningPOW(Block block) {

		byte[] hashedHeader = getHashedHeader(block);
		byte[] random32Bit = new byte[BIT32];
		
		SecureRandom nonce = new SecureRandom();//TODO Vielleicht die SecureRandom con ECDSA nutzen??? -> Damit es nur einmal defeniert worden ist...
		BigInteger target = new BigInteger(block.getDifficulty(), HEX);
		BigInteger challenge;

		while (RUNNING) {
			nonce.nextBytes(random32Bit);
			byte[] concatedByte = concat(hashedHeader, random32Bit);

			// For scrypt hashine!!! -> May change if possible???
			byte[] sha256Hashed = SHA.getMessageDigest(SHA256).digest(concatedByte);
			challenge = new BigInteger(BIG_ENDIAN_ALWAYS_POSITIVE, sha256Hashed);

			// Enable for test use only!!! -> Delete
//			   System.err.println("Challenge: " + challenge.toString().length());
//			   System.err.println("Target:    " + target.toString().length());
			   System.err.println("Challenge: " + challenge.toString());
			   System.err.println("Target:    " + target.toString());

			if (compareBigInteger(challenge, target)) {
				return createNewBlockHeader(challenge, random32Bit, block.getDifficulty(), block.getHeight());
			}
		}
	}

	private static Block createNewBlockHeader(BigInteger newHashValue, byte[] nonce, String newDifficulty, int blockHeight) {
		
		//Block header.
		Block newBlock = new Block();
		newBlock.setNewHashValue(newHashValue.toString());
		newBlock.setNonce(new BigInteger(BIG_ENDIAN_ALWAYS_POSITIVE, nonce).toString(HEX));
		newBlock.setTimestamp(System.currentTimeMillis());
		newBlock.setDifficulty(newDifficulty);

		// TODO Müssen noch immplementiert werden und sich überlegen
		// wie???
		newBlock.setVersion(testVersion);
		newBlock.setHashedMerkleRoot("0");
		// ///////////////////////////////////////////////////
		
		//Block additional information.
		newBlock.setHeight(++blockHeight);

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
		byte[] difficulty = lastblock.getDifficulty().getBytes();

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
