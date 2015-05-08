package org.educoins.core.miner;


import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.educoins.core.client.Transaction;


public class Block {
	
	private final static long DEFAULT_VALUE= 0;
//	private final static String DEFAULT_DIFFICULTY_30 = "ffffffffffffffffffffffffffffff";TODO Delete ???
	private final static String DEFAULT_DIFFICULTY_30 = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
	private final static String GENESIS_BLOCK = "Genesis Block";
	private final static int HEX = 16;

	// BlockHeader parameters for the PoW algorithm
	// https://litecoin.info/Block_header
	private long version;
	private String hashPrevBlock;
	private String hashMerkleRoot;
	private long timestamp;
	private BigInteger difficulty;
	private BigInteger nonce;

	// Additional block information
	private long id;
	private int height;
	private int nTx;// Count of all transactions

	private List <Transaction> transactions;

	
	public Block(){
		//Genesis Block and default values
		this.version = DEFAULT_VALUE;
		this.hashPrevBlock = GENESIS_BLOCK;
		this.hashMerkleRoot = Long.toString(DEFAULT_VALUE);
		this.timestamp = DEFAULT_VALUE;
		this.difficulty = new BigInteger(DEFAULT_DIFFICULTY_30, HEX);
		this.nonce = BigInteger.valueOf(DEFAULT_VALUE);		
	}
	
	public void addTransaction(List<Transaction> transactions) {
		this.transactions = transactions;
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

	public BigInteger getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(BigInteger difficulty) {
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
							 "nonce = %d \n" +
							 "\n\n" +
							 "nTx = %d \n", +
							 version, hashPrevBlock, hashMerkleRoot, timestamp, difficulty,	nonce, nTx);

		
	}

}
