package org.educoins.core;

import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;
import org.educoins.core.utils.Threading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Miner implements IBlockListener, IBlockReceiver {

	private static final int BIT32 = 32;
	private static Logger logger = LoggerFactory.getLogger(Miner.class);
	private BlockChain blockChain;
	private CopyOnWriteArrayList<IPoWListener> powListeners;
	private final Set<IBlockListener> blockListeners = new HashSet<>();

	public Miner() {
		this.powListeners = new CopyOnWriteArrayList<>();
	}
	
	public void setBlockChain(BlockChain blockChain){
		this.blockChain = blockChain;
		this.blockChain.addBlockListener(this);
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
			if(listener != null){
				listener.foundPoW(block);
			}
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

			logger.info("Starting mining process");
			do {
				nonceGenerator.nextBytes(nonce);
				this.block.setNonce(ByteArray.convertToInt(nonce));

				challenge = this.block.hash();

				if (challenge.compareTo(targetThreshold) > 0) {
					// System.out.println("Found smaller challenge! target: " +
					// FormatToScientifc.format(targetThreshold, 1)
					// + " | challenge: " + FormatToScientifc.format(challenge,
					// 1));
				}
				// System.out.println("challenge: " +
				// ByteArray.convertToString(challenge.getBytes())
				// + " | targetThreshold: " +
				// ByteArray.convertToString(targetThreshold.getBytes()));

			} while (this.active && challenge.compareTo(targetThreshold) < 0);

			if (this.active) {
				logger.info("Found a sufficient PoW hash: {}", challenge.toString());
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

	@Override
	public void addBlockListener(IBlockListener blockListener) {
		this.blockListeners.add(blockListener);
	}

	@Override
	public void removeBlockListener(IBlockListener blockListener) {
		this.blockListeners.remove(blockListener);
	}

	@Override
	public void receiveBlocks(Sha256Hash from) {
		receiveBlocks();
	}

	public void receiveBlocks() {

		Block latestBlock;
		try {
			latestBlock = this.blockChain.getLatestBlock();
			logger.info("Received Block: " + latestBlock.toString());
			Threading.run(() -> blockListeners.forEach(iBlockListener -> iBlockListener.blockReceived(latestBlock)));
		} catch (BlockNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
