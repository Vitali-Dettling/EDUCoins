package org.educoins.miner;


import org.educoins.core.*;
import org.educoins.core.utils.*;

import java.security.SecureRandom;
import java.util.concurrent.CopyOnWriteArrayList;

public class Miner implements IBlockListener {

	private static final int BIT32 = 32;
	
	private BlockChain blockChain;
	private CopyOnWriteArrayList<IPoWListener> powListeners;

	public Miner(BlockChain blockChain) {
		
		this.blockChain = blockChain;
		this.blockChain.addBlockListener(this);
		this.powListeners = new CopyOnWriteArrayList<>();
		this.addPoWListener(this.blockChain);
	}
	
	public void addPoWListener(IPoWListener powListener) {
		synchronized (this) {
			this.powListeners.add(powListener);
		}
	}
	
	public void removePoWListener(IPoWListener powListener) {
		synchronized (this) {
			this.powListeners.remove(powListener);
		}
	}
	
	public void notifyFoundPoW(Block block) {
		for (int i = 0; i < this.powListeners.size(); i++) {
			IPoWListener listener = this.powListeners.get(i);
			listener.foundPoW(block);
		}
	}

	@Override
	public void blockReceived(Block block) {
		Thread powThread = new PoWThread(block.copy());
		powThread.start();

		//Thread powThread2 = new PoWThread(block.copy());
		//powThread2.start();
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

			Sha256Hash targetThreshold = this.block.getBits();
			Sha256Hash challenge;
			
			do {
				nonceGenerator.nextBytes(nonce);
				this.block.setNonce(ByteArray.convertToInt(nonce));

				challenge = this.block.hash();
			
//				System.out.println("nonce: " + ByteArray.convertToString(nonce) + " | challenge: " + ByteArray.convertToString(challenge)
//						+ " | targetThreshold: " + ByteArray.convertToString(targetThreshold));
			} while (this.active && challenge.compareTo(targetThreshold) > 0);

			if (this.active) {
				// synchronize PoWThreads to avoid FileNotFoundException
				synchronized (this) {
					notifyFoundPoW(block);
				}
			}
			
			blockChain.removeBlockListener(this);
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
