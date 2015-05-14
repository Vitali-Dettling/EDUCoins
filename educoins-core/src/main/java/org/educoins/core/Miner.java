package org.educoins.core;

import java.security.SecureRandom;

import org.educoins.core.cryptography.IHasher;
import org.educoins.core.utils.ByteArray;

public class Miner implements IBlockListener {

	private IBlockReceiver blockReceiver;
	private IBlockTransmitter blockTransmitter;

	private IHasher hasher;

	public Miner(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter, IHasher hasher) {
		this.blockReceiver = blockReceiver;
		this.blockTransmitter = blockTransmitter;

		this.hasher = hasher;

		this.blockReceiver.addBlockListener(this);
	}

	@Override
	public void blockReceived(Block block) {
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

			this.blockReceiver.addBlockListener(this);

			this.active = true;
		}

		private Block prepareBlock() {
			Block block = new Block();
			// TODO [joeren]: which version?! Temporary take the version of the
			// previous block.
			block.setVersion(this.prevBlock.getVersion());
			block.setHashPrevBlock(ByteArray.convertToString(this.prevBlock.hash(hasher), 16));
			// TODO [joeren]: calculate hash merkle root! Temporary take the
			// hash merkle root of the previous block.
			block.setHashMerkleRoot(this.prevBlock.getHashMerkleRoot());
			block.setTime(System.currentTimeMillis());
			// TODO [joeren]: move vitali's solution for recalculating bits!
			block.setBits(this.prevBlock.getBits());

			return block;
		}

		@Override
		public void run() {
			Block block = this.prepareBlock();

			SecureRandom nonceGenerator = new SecureRandom();
			byte[] nonce = new byte[4];

			byte[] targetThreshold = Block.getTargetThreshold(this.prevBlock.getBits());

			do {
				nonceGenerator.nextBytes(nonce);
				block.setNonce(ByteArray.convertToInt(nonce));

			} while (this.active && ByteArray.compare(block.hash(hasher), targetThreshold) > 0);

			if (this.active) {
				this.blockTransmitter.transmitBlock(block);
			}

			this.blockReceiver.removeBlockListener(this);

		}

		@Override
		public void blockReceived(Block block) {
			this.active = false;
		}
	}

}
