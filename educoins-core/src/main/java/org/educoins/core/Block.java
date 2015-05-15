package org.educoins.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.educoins.core.cryptography.IHasher;
import org.educoins.core.utils.ByteArray;

public class Block {

	private int version;
	private String hashPrevBlock;
	private String hashMerkleRoot;
	private long time;
	private String bits;
	private long nonce;

	private int transactionsCount;
	private List<ATransaction> transactions;

	public Block() {
		this.transactions = new ArrayList<>();
		this.transactionsCount = this.transactions.size();
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getHashPrevBlock() {
		return this.hashPrevBlock;
	}

	public void setHashPrevBlock(String hashPrevBlock) {
		this.hashPrevBlock = hashPrevBlock;
	}

	public String getHashMerkleRoot() {
		return this.hashMerkleRoot;
	}

	public void setHashMerkleRoot(String hashMerkleRoot) {
		this.hashMerkleRoot = hashMerkleRoot;
	}

	public long getTime() {
		return this.time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getBits() {
		return this.bits;
	}

	public void setBits(String bits) {
		this.bits = bits;
	}

	public long getNonce() {
		return this.nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public int getTransactionsCount() {
		return this.transactionsCount;
	}

	public List<ATransaction> getTransactions() {
		// [joeren]: return just a copy of the transaction list, because of
		// potential effects with transactionsCount
		if (this.transactions != null) {
			return new ArrayList<ATransaction>(this.transactions);
		}
		return null;
	}

	public void setTransactions(List<ATransaction> transactions) {
		this.transactions = transactions;
		if (this.transactions == null) {
			this.transactionsCount = 0;
		} else {
			this.transactionsCount = this.transactions.size();
		}
	}

	public void addTransaction(ATransaction transaction) {
		if (this.transactions == null) {
			this.transactions = new ArrayList<>();
		}
		this.transactions.add(transaction);
		this.transactionsCount = this.transactions.size();
	}

	public void addTransactions(Collection<ATransaction> transactions) {
		if (this.transactions == null) {
			this.transactions = new ArrayList<>();
		}
		this.transactions.addAll(transactions);
		this.transactionsCount = this.transactions.size();
	}

	public byte[] hash(IHasher hasher) {
		return Block.hash(this, hasher);
	}

	public static byte[] getTargetThreshold(String bits) {
		byte[] convertedBits = ByteArray.convertFromString(bits, 16);

		// split the bits byte array into variables
		byte[] var1 = new byte[3];
		System.arraycopy(convertedBits, 1, var1, 0, convertedBits.length - 1);
		byte[] var2 = { convertedBits[0] };

		// define factor 1 (h2h3h4h5h6h7)
		BigInteger factor1 = new BigInteger(1, var1);

		// calculate exponent
		BigInteger exponent = new BigInteger(1, var2);
		exponent = exponent.subtract(new BigInteger("3"));
		exponent = exponent.multiply(new BigInteger("8"));

		// calculate factor 2 (2^exponent)
		BigInteger factor2 = new BigInteger("2");
		factor2 = factor2.pow(exponent.intValue());

		// calculate product (factor1 * factor2) and return
		BigInteger product = factor1.multiply(factor2);
		byte[] expandedBits = product.toByteArray();
		return expandedBits;
	}

	public static byte[] hash(Block block, IHasher hasher) {
		// specify used header fields (in byte arrays)
		byte[] version = ByteArray.convertFromLong(block.version);
		byte[] hashPrevBlock = ByteArray.convertFromString(block.hashPrevBlock, 16);
		byte[] hashMerkleRoot = ByteArray.convertFromString(block.hashMerkleRoot, 16);
		byte[] time = ByteArray.convertFromLong(block.time);
		byte[] bits = ByteArray.convertFromString(block.bits, 16);
		byte[] nonce = ByteArray.convertFromLong(block.nonce);

		// concatenate used header fields
		byte[] concatenatedHeaderFields = ByteArray.concatByteArrays(version, hashPrevBlock, hashMerkleRoot, time,
				bits, nonce);

		// hash concatenated header fields and return
		byte[] hash = hasher.hash(hasher.hash(concatenatedHeaderFields));
		return hash;
	}

}
