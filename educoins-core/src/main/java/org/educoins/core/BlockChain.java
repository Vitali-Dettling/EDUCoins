package org.educoins.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Deserializer;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class BlockChain implements IBlockListener, ITransactionListener, IPoWListener {

	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
	private static final int IN_SECONDS = 1000;
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final int HEX = 16;
	private static final int RESET_BLOCKS_COUNT = 0;
	private static final int DEFAULT_REWARD = 10;
	private static final int ZERO = 0;

	private int blockCounter;
	private IBlockReceiver blockReceiver;
	private IBlockTransmitter blockTransmitter;
	private List<IBlockListener> blockListeners;
	private ITransactionReceiver transactionReceiver;
	private ITransactionTransmitter transactionTransmitter;
	private List<ITransactionListener> transactionListeners;
	private List<Transaction> transactions;
	private Wallet wallet;
	private Block newBlock;
	private Verification verification;
	private IBlockStore store;
	
	private String publicKey;

	public BlockChain(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter, ITransactionReceiver transactionReceiver, ITransactionTransmitter transactionTransmitter, IBlockStore senderBlockStore) {
		
		this.wallet = new Wallet();
		this.blockListeners = new ArrayList<>();
		this.blockReceiver = blockReceiver;
		this.blockTransmitter = blockTransmitter;
		this.blockReceiver.addBlockListener(this);
		this.transactionListeners = new ArrayList<>();
		this.transactionReceiver = transactionReceiver;
		this.transactionTransmitter = transactionTransmitter;
		this.transactionReceiver.addTransactionListener(this);
		this.transactions = new ArrayList<>();
		this.verification = new Verification(this.wallet, this);
		this.store = senderBlockStore;
		
		this.blockCounter = RESET_BLOCKS_COUNT;
	}
	
	public Wallet getWallet() {
		return this.wallet;
	}
	
	public void addBlockListener(IBlockListener blockListener) {
		this.blockListeners.add(blockListener);
	}

	public void removeBlockListener(IBlockListener blockListener) {
		this.blockListeners.remove(blockListener);
	}
	
	public void notifyBlockReceived(Block newBlock) {
		for (IBlockListener blockListener : blockListeners) {
			blockListener.blockReceived(newBlock);
		}
	}
	
	@Override
	public void blockReceived(Block block) {
		if(this.verification.verifyBlock(block)){
			Block newBlock = prepareNewBlock(block);
			notifyBlockReceived(newBlock);
			List<Transaction> transactions = block.getTransactions();
			if (transactions != null) {
				for (Transaction transaction : transactions) {
					notifyTransactionReceived(transaction);
				}
			}
		}
	}
	
	public void addTransactionListener(ITransactionListener transactionListener) {
		this.transactionListeners.add(transactionListener);
	}
	
	public void removeTransactionListener(ITransactionListener transactionListener) {
		this.transactionListeners.remove(transactionListener);
	}
	
	public void notifyTransactionReceived(Transaction transaction) {
		for (int i = 0; i < this.transactionListeners.size(); i++) {
			ITransactionListener listener = this.transactionListeners.get(i);
			listener.transactionReceived(transaction);
		}
	}
	
	public void sendTransaction(Transaction transaction) {
		this.transactionTransmitter.transmitTransaction(transaction);
	}
	
	@Override
	public void transactionReceived(Transaction transaction) {
		ETransaction type = transaction.whichTransaction();
		if (type == ETransaction.REGULAR) {
			if (this.verification.verifyRegularTransaction(transaction)) {
				this.transactions.add(transaction);
			}
		} else if (type == ETransaction.APPROVED) {
			if (this.verification.verifyApprovedTransaction(transaction)) {
				this.transactions.add(transaction);
			}
		}
	}
	
	@Override
	public void foundPoW(Block block) {
		System.out.println("Block: " + block.getHashPrevBlock());
		this.store.put(block);
		//this.blockTransmitter.transmitBlock(block);
		this.blockReceiver.receiveBlocks();
	}
	
	public Block prepareNewBlock(Block currentBlock) {
		this.newBlock = new Block();
		// TODO [joeren]: which version?! Temporary take the version of the
		// previous block.
		this.newBlock.setVersion(currentBlock.getVersion());
		this.newBlock.setHashPrevBlock(ByteArray.convertToString(currentBlock.hash(), 16));
		// TODO [joeren]: calculate hash merkle root! Temporary take the
		// hash merkle root of the previous block.
		this.newBlock.setHashMerkleRoot(currentBlock.getHashMerkleRoot());
		
		this.newBlock.addTransaction(coinbaseTransaction(currentBlock));
		this.newBlock.addTransactions(this.transactions);
		this.transactions.clear();
		
		return retargedBits(currentBlock);
	}
	
	
	private Transaction coinbaseTransaction(Block currentBlock) {

		//TODO [Vitali] Needs to be changes just for testing.
		if(this.publicKey == null){
			this.publicKey = this.wallet.getPublicKey();
		}
		
		//TODO [Vitali] lockingScript procedure has to be established, which fits our needs...
		String lockingScript = publicKey;		
		
		//Input is empty because it is a coinbase transaction.
		int newReward = rewardCalculator(currentBlock);
		Output output = new Output(newReward, publicKey, lockingScript);
		
		CoinbaseTransaction transaction = new CoinbaseTransaction(); 
		transaction.addOutput(output);
		return transaction;
	}
	
	
	private int rewardCalculator(Block currentBlock){
		
		int newReward = ZERO;
		int lastApprovedEDUCoins = findAllApprovedEDUCoins(currentBlock);
		
		//TODO[Vitali] Einen besseren mathematischen Algorithmus ausdengen, um die ausschütung zu bestimmen!!!
		if(DEFAULT_REWARD == lastApprovedEDUCoins){
			newReward = DEFAULT_REWARD;
		}else if(DEFAULT_REWARD > lastApprovedEDUCoins){
			newReward = lastApprovedEDUCoins + 2;
		}else if(DEFAULT_REWARD < lastApprovedEDUCoins){
			newReward = DEFAULT_REWARD - 2;
		}		

		return newReward;
	}
		
	
	private int findAllApprovedEDUCoins(Block currentBlock){
		
		int latestApprovedEDUCoins = ZERO;
		List<Transaction> latestTransactions = currentBlock.getTransactions();
		
		//TODO[Vitali] Might not be 100% correct???ß
		for(Transaction transaction : latestTransactions){
			List<Approval> approvals = transaction.getApprovals();
			for(Approval approval : approvals){
				latestApprovedEDUCoins += approval.getAmount();
			}
		}
		
		return latestApprovedEDUCoins;
	}
	
	
	/**
	 * Bitcoin explanation: Mastering Bitcoin 195
	 * Every 2,016 blocks, all nodes retarget the proof-of-work difficulty. The
	 * equation for retargeting difficulty measures the time it took to find the
	 * last 2,016 blocks and compares that to the expected time of 20,160 minutes.
	 * 
	 * New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks / 20160 minutes)
	 * */
	private Block retargedBits(Block previousBlock) {
		
		if(this.blockCounter == CHECK_AFTER_BLOCKS){
			long currentTime = System.currentTimeMillis();
			long allBlocksSinceLastTime = previousBlock.getTime();
			BigDecimal oldDifficulty = new BigDecimal(new BigInteger(previousBlock.getBits(), HEX)).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);
			BigDecimal actualBlockTime = BigDecimal.valueOf(currentTime - allBlocksSinceLastTime).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);	
			
			// New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks / 20160 minutes)
			BigDecimal newDifficulty = oldDifficulty.multiply(actualBlockTime.divide(BigDecimal.valueOf(DESIRED_BLOCK_TIME),
					BigDecimal.ROUND_HALF_DOWN).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP));
			
			this.newBlock.setBits(newDifficulty.toBigInteger().toString(HEX));			
			this.newBlock.setTime(currentTime);
			this.blockCounter = RESET_BLOCKS_COUNT;
		}
		else{
			//The last time stamp since the last retargeting of the difficulty. 
			this.newBlock.setTime(previousBlock.getTime());	
			this.newBlock.setBits(previousBlock.getBits());	
		}
		this.blockCounter++;
		return this.newBlock;
	}
	
	//TODO [Vitali] Method needs to be deleted as soon as the DB will be introduced.
	public Block getPreviousBlock(Block currentBlock) {

		String previousBlockHash = currentBlock.getHashPrevBlock();
		byte[] previousBlock = ByteArray.convertFromString(previousBlockHash);
		System.out.println("Get:   " + previousBlockHash);
		return this.store.get(currentBlock);
		
	}

}
