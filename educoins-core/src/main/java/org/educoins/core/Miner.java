package org.educoins.core;


import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.FormatToScientifc;
import org.educoins.core.utils.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.concurrent.CopyOnWriteArrayList;

public class Miner implements IBlockListener {

	private static final int BIT32 = 32;
	Logger log = LoggerFactory.getLogger(Miner.class);
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
		for (IPoWListener listener : this.powListeners) {
			listener.foundPoW(block);
		}
	}

	@Override
	public void blockReceived(Block block) {
		new PoWThread(block.copy()).start();
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
				
				if (challenge.compareTo(targetThreshold) > 0)
				{
					//System.out.println("Found smaller challenge! target: " + FormatToScientifc.format(targetThreshold, 1)
							//+ " | challenge: " + FormatToScientifc.format(challenge, 1));
				}
				// System.out.println("challenge: " + ByteArray.convertToString(challenge.getBytes())
//				+ " | targetThreshold: " + ByteArray.convertToString(targetThreshold.getBytes()));
				
			} while (this.active && challenge.compareTo(targetThreshold) < 0);

			if (this.active) {
				notifyFoundPoW(block);
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
