package org.educoins.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.educoins.core.cryptography.IHasher;
import org.educoins.core.utils.ByteArray;

public class Miner implements IBlockListener {

	private static final int CHECK_AFTER_BLOCKS = 5;
	private static final int RESET_BLOCKS_COUNT = 0;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 5;//TODO Change time to 30 which is equal to 5 min...
	private static final int MILLISECONDS = 1000;
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * CHECK_AFTER_BLOCKS * MILLISECONDS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	
	private static final int BIT32 = 32;
	private static final int HEX = 16;
	
	private IBlockReceiver blockReceiver;
	private IBlockTransmitter blockTransmitter;
	
	private static int blockCounter;

	private IHasher hasher;

	public Miner(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter, IHasher hasher) {
		this.blockReceiver = blockReceiver;
		this.blockTransmitter = blockTransmitter;

		this.hasher = hasher;
		this.blockReceiver.addBlockListener(this);	
		Miner.blockCounter = RESET_BLOCKS_COUNT;
	}

	@Override
	public void blockReceived(Block block) {
		// TODO [joeren]: delete temp message
		Thread powThread = new PoWThread(blockReceiver, blockTransmitter, block, hasher);
		powThread.start();
	}

	
	private static class PoWThread extends Thread implements IBlockListener {
		
		private IBlockReceiver blockReceiver;
		private IBlockTransmitter blockTransmitter;

		private Block prevBlock;
		private IHasher hasher;
		private boolean active;

		public PoWThread(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter, Block prevBlock,
				IHasher hasher) {
			this.blockReceiver = blockReceiver;
			this.blockTransmitter = blockTransmitter;

			this.prevBlock = prevBlock;
			this.hasher = hasher;
			this.active = true;
		}

		private Block prepareNewBlock() {
			
			Block newBlock = new Block();
			// TODO [joeren]: which version?! Temporary take the version of the
			// previous block.
			newBlock.setVersion(this.prevBlock.getVersion());
			newBlock.setHashPrevBlock(ByteArray.convertToString(this.prevBlock.hash(hasher), 16));
			// TODO [joeren]: calculate hash merkle root! Temporary take the
			// hash merkle root of the previous block.
			newBlock.setHashMerkleRoot(this.prevBlock.getHashMerkleRoot());
			
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
			
			this.blockReceiver.addBlockListener(this);
			
			Block newBlock = prepareNewBlock();
			
			SecureRandom nonceGenerator = new SecureRandom();
			byte[] nonce = new byte[BIT32];
			byte[] targetThreshold = Block.getTargetThreshold(newBlock.getBits());
			byte[] challenge;
			byte[] test;
			
			do {
				nonceGenerator.nextBytes(nonce);
				newBlock.setNonce(ByteArray.convertToInt(nonce));
				
				challenge = newBlock.hash(hasher);
				//TODO [Vitali] Delete after testing.
//				System.err.println("Target   : " + targetThreshold.length);
//				System.err.println("Challenge: " + challenge.length);
				
				test = invertNegaitve(challenge);
				
				System.err.println("Target   : " + new BigInteger(targetThreshold));
				System.err.println("Challenge: " + new BigInteger(test));

			} while (this.active && ByteArray.compare(invertNegaitve(test), targetThreshold) > 0);

			if (this.active) {
				//reward(block);TODO commend in reward.
				// TODO [joeren]: delete output message
				System.out.println("Won :-)");
				this.blockTransmitter.transmitBlock(newBlock);
			} else {
				// TODO [joeren]: delete output message
				System.out.println("Loose :-(");
			}
			
			this.blockReceiver.removeBlockListener(this);
		}
		

		private static byte[] invertNegaitve(byte[] toInvertBitInteger) {
			boolean isNegative = (toInvertBitInteger[0] & 0x80) == 0x80;
			if (isNegative)
				toInvertBitInteger[0] &= 0x7f;
			return toInvertBitInteger;
		}

		
	
		/*
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
		}*/
		
		


		
		

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
