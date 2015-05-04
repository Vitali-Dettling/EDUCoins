package org.educoins.core.miner;

import java.math.BigInteger;

public class Block {
	
	private final static long DEFAULT_VALUE= 0;
	private final static String DEFAULT_DIFFICULTY_30 = "ffffffffffffffffffffffffffffff";
	private final static String GENESIS_BLOCK = "Genesis Block";

	// BlockHeader parameters for the PoW algorithm
	// https://litecoin.info/Block_header
	private long version;
	private String hashPrevBlock;
	private String hashMerkleRoot;
	private long timestamp;
	private String difficulty;
	private BigInteger nonce;

	// Additional block information
	private long id;
	private int height;
	private int nTx;// Count of all transactions

	// List<? extends Appendix> getAppendages(); !!! -> Will we need later !!!

	
	public Block(){
		//Genesis Block and default values
		this.version = DEFAULT_VALUE;
		this.hashPrevBlock = GENESIS_BLOCK;
		this.hashMerkleRoot = Long.toString(DEFAULT_VALUE);
		this.timestamp = DEFAULT_VALUE;
		this.difficulty = DEFAULT_DIFFICULTY_30;
		this.nonce = BigInteger.valueOf(DEFAULT_VALUE); 
	}
	
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getHashedPrevBlock() {
		return hashPrevBlock;
	}

	public void setNewHashValue(String prevBlock) {
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

	public void setTimestamp(long timeStamp) {
		this.timestamp = timeStamp;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public BigInteger getNonce() {
		return nonce;
	}

	public void setNonce(BigInteger nonce) {
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
		return String.format("version = %d; \n" + 
							 "hashPrevBlock = %s; \n" + 
							 "hashMerkleRoot = %s; \n" + 
							 "timestamp = %d; \n" + 
							 "difficulty = %s; \n" +
							 "nonce = %d \n",
							 version, hashPrevBlock, hashMerkleRoot, timestamp, difficulty,	nonce);
	}

}
