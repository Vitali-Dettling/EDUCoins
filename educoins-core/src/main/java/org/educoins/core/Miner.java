package org.educoins.core;

import org.educoins.core.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.concurrent.CopyOnWriteArrayList;

public class Miner implements IBlockListenerMiner {

    private static final int BIT32 = 32;
    private static Logger logger = LoggerFactory.getLogger(Miner.class);
    private final BlockChain blockChain;
    private CopyOnWriteArrayList<IPoWListener> powListeners;

    public Miner(BlockChain blockChain) {
        this.powListeners = new CopyOnWriteArrayList<>();
        this.blockChain = blockChain;
        this.blockChain.addBlockListenerMiner(this);
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
            if (listener != null) {
                listener.foundPoW(block);
            }
        }
    }

    @Override
    public void blockReceived(Block latestBlock) {
        logger.info("Received Block: " + latestBlock.toString());
        Threading.run(new PoWThread(latestBlock.copy()));
    }

    private class PoWThread extends Thread implements IBlockListener {

        private boolean active;
        private Block block;

        public PoWThread(Block block) {
            this.setName("PoWThread RUNNING");
            this.block = block;
            this.active = true;
        }

        @Override
        public void run() {
            Miner.this.blockChain.addBlockListener(this);

            SecureRandom nonceGenerator = new SecureRandom();
            byte[] nonce = new byte[BIT32];

            Sha256Hash targetThreshold = this.block.getBits();
            Sha256Hash challenge;

            logger.info("Starting mining process");
            do {
                nonceGenerator.nextBytes(nonce);
                this.block.setNonce(ByteArray.convertToInt(nonce));

                challenge = this.block.hash();

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            } while (this.active && challenge.compareTo(targetThreshold) < 0);

            if (this.active) {
                logger.info("Found a sufficient PoW hash: {}", challenge.toString());
                notifyFoundPoW(block);
            } else {
                logger.info("Mining interrupted!");
            }

            Miner.this.blockChain.removeBlockListener(this);
        }

        @Override
        public void blockReceived(Block block) {

            this.active = false;
        }
    }
}
