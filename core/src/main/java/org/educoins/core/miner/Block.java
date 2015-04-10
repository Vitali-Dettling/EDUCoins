package org.educoins.core.miner;

import java.util.List;


public class Block {

	//BlockHeader parameters for the PoW algorithm 
	//https://litecoin.info/Block_header
	private int version;
	private String hashPrevBlock;
	private String hashMerkleRoot;
	private int timeStamp;
	private int difficulty;
	private int nonce;
	
	//Additional block information
	private long id;
	private int height;
	private int nTx;
	// List<? extends Appendix> getAppendages(); !!! -> Will we need later !!!
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
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
	public int getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public int getNonce() {
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
	
}


