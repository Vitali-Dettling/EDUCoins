package org.educoins.core;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.utils.ByteArray;

public class Miner implements IBlockListener {

	private static final int BIT32 = 32;
	
	private BlockChain blockChain;
	private List<IPoWListener> powListeners;

	public Miner(BlockChain blockChain) {
		
		this.blockChain = blockChain;
		this.blockChain.addBlockListener(this);
		this.powListeners = new ArrayList<>();
		this.addPoWListener(this.blockChain);
	}
	
	public void addPoWListener(IPoWListener powListener) {
		this.powListeners.add(powListener);
	}
	
	public void removePoWListener(IPoWListener powListener) {
		this.powListeners.remove(powListener);
	}
	
	public void notifyFoundPoW(Block block) {
		for (int i = 0; i < this.powListeners.size(); i++) {
			IPoWListener listener = this.powListeners.get(i);
			listener.foundPoW(block);
		}
	}

	@Override
	public void blockReceived(Block block) {
		Thread powThread = new PoWThread(block);
		powThread.start();
	}
	
	private class PoWThread extends Thread implements IBlockListener {
		
		private boolean active;
		private Block block;

		public PoWThread(Block block) {
			this.setName("PoWThread");
			this.block = block;
			this.active = true;
		}
		

		@Override
		public void run() {
			
			blockChain.addBlockListener(this);
			
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
//				System.out.println("Won :-)");
				notifyFoundPoW(block);
				
			} else {
				// TODO [joeren]: delete output message
//				System.out.println("Loose :-(");
			}
			
			blockChain.removeBlockListener(this);
		}
		
		private byte[] invertNegaitve(byte[] toInvertBitInteger) {
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
