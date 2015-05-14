package org.educoins.core;

public final class GenesisBlock extends Block {

	private static final int VERSION = 2;
	private static final String HASH_PREV_BLOCK = "0000000000000000000000000";
	private static final String HASH_MERKLE_ROOT = "0000000000000000000000000";
	private static final long TIME = System.currentTimeMillis();
	private static final String BITS = "1e01ff3f";
	private static final long NONCE = 1114735442;
	
	public GenesisBlock() {
		this.setVersion(GenesisBlock.VERSION);
		this.setHashPrevBlock(GenesisBlock.HASH_PREV_BLOCK);
		this.setHashMerkleRoot(GenesisBlock.HASH_MERKLE_ROOT);
		this.setTime(GenesisBlock.TIME);
		this.setBits(GenesisBlock.BITS);
		this.setNonce(GenesisBlock.NONCE);
	}
	
}
