package org.educoins.core;

import org.educoins.core.cryptography.SHA256Hasher;
import org.educoins.core.utils.BinaryTree;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;

import java.util.*;

import java.util.*;

//import org.educoins.core.p2p.messages.MessageProtos;

public class Block {
	
	private static final int DEFAULT_REWARD = 10;
	private static final int ZERO = 0;
	private static final int VERSION = -1;//-1 if no version is set and also an error.
	private static final Sha256Hash HASH_PREV_BLOCK  = Sha256Hash.ZERO_HASH;
	private static final Sha256Hash HASH_MERKLE_ROOT = Sha256Hash.ZERO_HASH;
	private static final long TIME = System.currentTimeMillis();

	private static final byte[] BITS = ByteArray.convertFromString("1dffffff");
	private static final long NONCE = 1114735442;

	private int version;
	private Sha256Hash hashPrevBlock;
	private Sha256Hash hashMerkleRoot;
	private long time;
	private byte[] bits;
	private long nonce;
	private int transactionsCount;
	private List<Transaction> transactions;

	public Block() {
		this.setVersion(VERSION);
		this.setHashPrevBlock(HASH_PREV_BLOCK);
		this.setHashMerkleRoot(HASH_MERKLE_ROOT);
		this.setTime(TIME);
		bits = BITS;
		this.setNonce(NONCE);
		
		this.transactions = new ArrayList<>();
		this.transactionsCount = this.transactions.size();
	}

	public Block copy(){
		Block b = new Block();
		b.setBits(this.getBits());
		b.setTime(this.getTime());
		b.setHashMerkleRoot(this.getHashMerkleRoot());
		b.setHashPrevBlock(this.getHashPrevBlock());
		b.setNonce(this.getNonce());
		b.setTransactions(new ArrayList<Transaction>(this.getTransactions()));
		return b;
	}

	public int getVersion() {
		return this.version;
	}

    public void setVersion(int version) {
        this.version = version;
    }

    public Sha256Hash getHashPrevBlock() {
        return this.hashPrevBlock;
    }

    public void setHashPrevBlock(Sha256Hash hashPrevBlock) {
        this.hashPrevBlock = hashPrevBlock;
    }

    public Sha256Hash getHashMerkleRoot() {
        return this.hashMerkleRoot;
    }

    public void setHashMerkleRoot(Sha256Hash hashMerkleRoot) {
        this.hashMerkleRoot = hashMerkleRoot;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

	public Sha256Hash getBits() {
        byte[] mantisse = Arrays.copyOfRange(bits, 1, 4);
        int expInt = (int) bits[0];

        byte[] result = new byte[3 + expInt];
        Arrays.fill(result, (byte) 0);
        System.arraycopy(mantisse, 0, result, 0, 3);
        
        return Sha256Hash.wrap(result);
	}

	public void setBits(Sha256Hash inBits) {
		byte[] bits = inBits.getBytes();
        byte[] mantisse;
        byte[] exponent = new byte[1];

        int i = 0;
        for (i = 0; bits[i] == (byte) 0; i++); //count leading 0

        exponent[0] = (byte) (bits.length - i - 3);
        mantisse = Arrays.copyOfRange(bits, i, i + 3);

        this.bits = ByteArray.concatByteArrays(exponent, mantisse);
	}

    public long getNonce() {
        return this.nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public void calculateMerkleRoot() {
        if (this.getTransactionsCount() == 0) {
            this.setHashMerkleRoot(Sha256Hash.ZERO_HASH);
            return;
        }

        BinaryTree<Transaction> tree = new BinaryTree<>(getTransactions());
        setHashMerkleRoot(tree.getRoot().hash());
    }

    public int getTransactionsCount() {
        return this.transactionsCount;
    }

    public List<Transaction> getTransactions() {
        // [joeren]: return just a copy of the transaction list, because of
        // potential effects with transactionsCount
        if (this.transactions != null) {
            return new ArrayList<Transaction>(this.transactions);
        }
        return null;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        if (this.transactions == null) {
            this.transactionsCount = 0;
        } else {
            this.transactionsCount = this.transactions.size();
        }
        calculateMerkleRoot();
    }

    public void addTransaction(Transaction transaction) {
        if (this.transactions == null) {
            this.transactions = new ArrayList<>();
        }
        this.transactions.add(transaction);
        this.transactionsCount = this.transactions.size();
        calculateMerkleRoot();
    }

    public void addTransactions(Collection<Transaction> transactions) {
        if (this.transactions == null) {
            this.transactions = new ArrayList<>();
        }
        this.transactions.addAll(transactions);
        this.transactionsCount = this.transactions.size();
        calculateMerkleRoot();
    }

    public Sha256Hash hash() {
        return Block.hash(this);
    }

    /*public MessageProtos.Block toProto() {
        MessageProtos.Block.Builder builder = MessageProtos.Block.newBuilder();
        builder.setBits(Integer.parseInt(getBits()));
        builder.setVersion(getVersion());
        builder.setMerkleRoot(getHashMerkleRoot());
        builder.setPrevBlock(getHashPrevBlock());
        builder.setNonce((int) getNonce());
        builder.setTimestamp(getTime());
        builder.setTxnCount(getTransactionsCount());
        int index = 0;
        transactions.forEach(tnx -> builder.setTxns(index, tnx.getApprovalsCount()));

        return builder.build();
    }*/
    
	public int rewardCalculator(){
		
		int newReward;
		int lastApprovedEDUCoins = findAllApprovedEDUCoins();
		
		//TODO[Vitali] Einen besseren mathematischen Algorithmus ausdengen, um die ausschütung zu bestimmen!!!
		if(DEFAULT_REWARD == lastApprovedEDUCoins) {
			newReward = DEFAULT_REWARD;
		}else if(DEFAULT_REWARD > lastApprovedEDUCoins) {
			newReward = lastApprovedEDUCoins + 2;
		}else {
			newReward = DEFAULT_REWARD - 2;
		}		

		return newReward;
	}
	
	private int findAllApprovedEDUCoins(){
		
		int approvedEDUCoins = 0;
		
		//TODO[Vitali] Might not be 100% correct???ß
		for(Transaction transaction : this.getTransactions()){
			for(Approval approval : transaction.getApprovals()){
				approvedEDUCoins += approval.getAmount();
			}
		}
		
		return approvedEDUCoins;
	}
    public static Sha256Hash hash(Block block) {
		// specify used header fields (in byte arrays)
		byte[] version = ByteArray.convertFromLong(block.version);
		byte[] hashPrevBlock = block.hashPrevBlock.getBytes();
		byte[] hashMerkleRoot = block.hashMerkleRoot.getBytes();
		byte[] time = ByteArray.convertFromLong(block.time);
		byte[] bits = block.bits;
		byte[] nonce = ByteArray.convertFromLong(block.nonce);

		// concatenate used header fields
		byte[] concatenatedHeaderFields = ByteArray.concatByteArrays(version, hashPrevBlock, hashMerkleRoot, time,
				bits, nonce);
		
		// hash concatenated header fields and return
		byte[] hash = SHA256Hasher.hash(SHA256Hasher.hash(concatenatedHeaderFields));
		return Sha256Hash.wrap(hash);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        if (version != block.version) return false;
        if (time != block.time) return false;
        if (nonce != block.nonce) return false;
        if (!hashPrevBlock.equals(block.hashPrevBlock)) return false;
        if (!hashMerkleRoot.equals(block.hashMerkleRoot)) return false;
        return bits.equals(block.bits);
    }


    @Override
	public int hashCode() {
		int result = version;
		result = 31 * result + hashPrevBlock.hashCode();
		result = 31 * result + hashMerkleRoot.hashCode();
		result = 31 * result + (int) (time ^ (time >>> 32));
		result = 31 * result + bits.hashCode();
		result = 31 * result + (int) (nonce ^ (nonce >>> 32));
		return result;
	}
}
