package org.educoins.core.miner;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import Transactions.Output;
import Transactions.Transaction;


public class Block {
	
	private final static long DEFAULT_VALUE = 0;
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
	
	private List <Transaction> transactions;

	/**
	 * https://en.bitcoin.it/wiki/History
	 * */
	public Block(){
		//Genesis Block and default values
		this.version = DEFAULT_VALUE;
		this.hashPrevBlock = GENESIS_BLOCK;
		this.hashMerkleRoot = GENESIS_BLOCK;
		this.timestamp = DEFAULT_VALUE;
		this.difficulty = new BigInteger(DEFAULT_DIFFICULTY_30, HEX);
		this.nonce = BigInteger.valueOf(DEFAULT_VALUE);		
		this.height = (int) DEFAULT_VALUE;
		this.transactions = new ArrayList<Transaction>();
	}
	
	public void addTransaction(Transaction transactions) {
		this.transactions.add(transactions);
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

			 //TODO Einfachere und intuitivare Ausgabe implementieren...
	@Override//TODO checks einbauen, damit auch die ausgaben der wahrheit entsprechen....
	public String toString() {
		
		String blockHeader = String.format("\nBlock Header\n" +
							 "version = %d; \n" + 
							 "hashPrevBlock = %s; \n" + 
							 "hashMerkleRoot = %s; \n" + 
							 "timestamp = %d; \n" + 
							 "difficulty = %d; \n" +
							 "nonce = %d \n" +
							 "\n", + 
							 version, hashPrevBlock, hashMerkleRoot, timestamp, difficulty, nonce);
		
		String additionalInformation = 	 String.format("\nBlock Additional Information\n" +
										 "Block height = %d \n" + 
										 "\n", + 
										  height);
		String transactions = "";
		if(this.transactions.size() > 0){
			transactions = String.format("\nTransactions\n" +
								"nTx = %d \n" +
								"Amount: %d \n" +
								"EDUCoin Address: %s \n" +
								"\n", + 
								this.transactions.size(), 
								this.transactions.get(0).getOutputs().get(0).getAmount(), 
								this.transactions.get(0).getOutputs().get(0).getToAddress());
	
		}
		
		
		return blockHeader + additionalInformation + transactions;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
