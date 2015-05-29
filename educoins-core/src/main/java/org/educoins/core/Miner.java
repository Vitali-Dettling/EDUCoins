package org.educoins.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.educoins.core.utils.ByteArray;

public class Miner implements IBlockListener {

	private static final int RESET_BLOCKS_COUNT = 0;
	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
	private static final int IN_SECONDS = 1000;
	//TODO aus irgend einem Grund funktioniert die Desired Time nicht? Um so höcher diese ist um so unwahrscheinlicher kalibriert sich die Difficulty???
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	
	private static final int BIT32 = 32;
	private static final int HEX = 16;
	
	private static int blockCounter;
	private static String publicKey;
	
	private static BlockChain blockChain;

	public Miner(BlockChain blockChain, Wallet wallet) {
		
		Miner.publicKey =  wallet.getPublicKey();
		
		Miner.blockCounter = RESET_BLOCKS_COUNT;
		
		Miner.blockChain = blockChain;
		
		Miner.blockChain.addBlockListener(this);
	}

	@Override
	public void blockReceived(Block block) {
		Thread powThread = new PoWThread(block);
		powThread.start();
	}

	
	private static class PoWThread extends Thread implements IBlockListener {
		

		private Block prevBlock;
		private boolean active;

		public PoWThread(Block prevBlock) {

			this.prevBlock = prevBlock;
			this.active = true;
		}

		private Block prepareNewBlock() {
			
			Block newBlock = new Block();
			// TODO [joeren]: which version?! Temporary take the version of the
			// previous block.
			newBlock.setVersion(this.prevBlock.getVersion());
			newBlock.setHashPrevBlock(ByteArray.convertToString(this.prevBlock.hash(), 16));
			// TODO [joeren]: calculate hash merkle root! Temporary take the
			// hash merkle root of the previous block.
			newBlock.setHashMerkleRoot(this.prevBlock.getHashMerkleRoot());
			
			newBlock.addTransaction(reward());
			
			return retargedBits(newBlock);
		}
		
		/**
		 * Bitcoin explanation: Mastering Bitcoin 195
		 * Every 2,016 blocks, all nodes retarget the proof-of-work difficulty. The
		 * equation for retargeting difficulty measures the time it took to find the
		 * last 2,016 blocks and compares that to the expected time of 20,160 minutes.
		 * 
		 * New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks / 20160 minutes)
		 * */
		//TODO [Vitali] Einigen ob Bits oder Difficulty, damit es einheitlich bleibt!!!
		private Block retargedBits(Block newBlock) {
			
			if(Miner.blockCounter == CHECK_AFTER_BLOCKS){
				long currentTime = System.currentTimeMillis();
				long allBlocksSinceLastTime = this.prevBlock.getTime();
				BigDecimal oldDifficulty = new BigDecimal(new BigInteger(this.prevBlock.getBits(), HEX)).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);
				BigDecimal actualBlockTime = BigDecimal.valueOf(currentTime - allBlocksSinceLastTime).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);	
				
				// New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks / 20160 minutes)
				BigDecimal newDifficulty = oldDifficulty.multiply(actualBlockTime.divide(BigDecimal.valueOf(DESIRED_BLOCK_TIME), BigDecimal.ROUND_HALF_DOWN).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP));
				
				newBlock.setBits(newDifficulty.toBigInteger().toString(HEX));			
				newBlock.setTime(currentTime);
				Miner.blockCounter = RESET_BLOCKS_COUNT;
			}
			else{
				//The last time stamp since the last retargeting of the difficulty. 
				newBlock.setTime(this.prevBlock.getTime());	
				newBlock.setBits(this.prevBlock.getBits());	
			}
			Miner.blockCounter++;
			return newBlock;
		}


		@Override
		public void run() {
			
			Miner.blockChain.addBlockListener(this);
			
			Block newBlock = prepareNewBlock();
			
			SecureRandom nonceGenerator = new SecureRandom();
			byte[] nonce = new byte[BIT32];
			byte[] targetThreshold = Block.getTargetThreshold(newBlock.getBits());
			byte[] challenge;
			byte[] challengePositive;
			
			do {
				nonceGenerator.nextBytes(nonce);
				newBlock.setNonce(ByteArray.convertToInt(nonce));
				
				challenge = newBlock.hash();
				//TODO [Vitali] Delete after testing.
//				System.err.println("Target   : " + targetThreshold.length);
//				System.err.println("Challenge: " + challenge.length);
				
				challengePositive = invertNegaitve(challenge);
				
				System.out.println("Target   : " + new BigInteger(targetThreshold));
				System.out.println("Challenge: " + new BigInteger(challengePositive));

			} while (this.active && ByteArray.compare(challengePositive, targetThreshold) > 0);

			if (this.active) {
				// TODO [joeren]: delete output message
				System.out.println("Won :-)");
				Miner.blockChain.transmitBlock(newBlock);
				
			} else {
				// TODO [joeren]: delete output message
				System.out.println("Loose :-(");
			}
			
			Miner.blockChain.removeBlockListener(this);
		}
		

		private static byte[] invertNegaitve(byte[] toInvertBitInteger) {
			boolean isNegative = (toInvertBitInteger[0] & 0x80) == 0x80;
			if (isNegative)
				toInvertBitInteger[0] &= 0x7f;
			return toInvertBitInteger;
		}

		private Transaction reward() {

			//TODO [Vitali] lockingScript procedure has to be established, which fits our needs...
			String lockingScript = EScripts.DUB.toString() + " " +
								   EScripts.HASH160.toString() + " " +
								   Miner.publicKey + " " +//TODO[Vitali] Might needs to be delete after real address entry!!!
								   EScripts.EQUALVERIFY.toString() + " " +
								   EScripts.CHECKSIG.toString();
			//Input is empty because it is a coinbase transaction.
			Output output = new Output(10, Miner.publicKey, lockingScript);
			
			RegularTransaction transaction = new RegularTransaction(); 
			transaction.addOutput(output);
			return transaction;
		}
		
		
		@Override
		public void blockReceived(Block block) {
			this.active = false;
		}
		
		@Override
		public String toString() {
			return "PoWThread";
		}
	}

}
