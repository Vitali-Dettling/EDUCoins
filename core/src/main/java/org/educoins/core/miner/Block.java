package org.educoins.core.miner;

public class Block {

	// BlockHeader parameters for the PoW algorithm
	// https://litecoin.info/Block_header
	private long version;
	private String hashPrevBlock;
	private String hashMerkleRoot;
	private long timestamp;
	private String difficulty;
	private long nonce;

	// Additional block information
	private long id;
	private int height;
	private int nTx;// Count of all transactions

	// List<? extends Appendix> getAppendages(); !!! -> Will we need later !!!

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getHashedPrevBlock() {
		return hashPrevBlock;
	}

	public void setHashedPrevBlock(String prevBlock) {
		this.hashPrevBlock = prevBlock;
	}

	public String getHashedMerkleRoot() {
		return hashMerkleRoot;
	}

	public void setHashedMerkleRoot(String merkleRoot) {
		this.hashMerkleRoot = merkleRoot;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	public int getnTx() {
		return nTx;
	}

	public void setnTx(int nTx) {
		this.nTx = nTx;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return String.format("Block[version=%d;" + "hashPrevBlock=%s;"
				+ "hashMerkleRoot=%s;timestamp=%d;difficulty=%s;nonce=%d]",
				version, hashPrevBlock, hashMerkleRoot, timestamp, difficulty,
				nonce);
	}

}
