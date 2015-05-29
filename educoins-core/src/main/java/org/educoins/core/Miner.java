package org.educoins.core;

import java.security.SecureRandom;

import org.educoins.core.utils.ByteArray;

public class Miner implements IBlockListener {

	private static final int BIT32 = 32;
	
	private static BlockChain blockChain;

	public Miner(BlockChain blockChain) {
		
		Miner.blockChain = blockChain;
		Miner.blockChain.addBlockListener(this);
	}


	@Override
	public void blockReceived(Block block) {
		Thread powThread = new PoWThread(block);
		powThread.start();
	}

	
	private static class PoWThread extends Thread implements IBlockListener {
		
		private boolean active;
		private Block block;

		public PoWThread(Block block) {
			this.block = block;
			this.active = true;
		}


		@Override
		public void run() {
			
			Miner.blockChain.addBlockListener(this);
			
			SecureRandom nonceGenerator = new SecureRandom();
			byte[] nonce = new byte[BIT32];
			byte[] targetThreshold = Block.getTargetThreshold(this.block.getBits());
			byte[] challenge;
			byte[] challengePositive;
			
			do {
				nonceGenerator.nextBytes(nonce);
				this.block.setNonce(ByteArray.convertToInt(nonce));
				
				challenge = this.block.hash();
				//TODO [Vitali] Delete after testing.
//				System.err.println("Target   : " + targetThreshold.length);
//				System.err.println("Challenge: " + challenge.length);
				
				challengePositive = invertNegaitve(challenge);
				
//				System.out.println("Target   : " + new BigInteger(targetThreshold));
//				System.out.println("Challenge: " + new BigInteger(challengePositive));

			} while (this.active && ByteArray.compare(challengePositive, targetThreshold) > 0);

			if (this.active) {
				// TODO [joeren]: delete output message
				System.out.println("Won :-)");
				Miner.blockChain.transmitBlock(this.block);
				
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
