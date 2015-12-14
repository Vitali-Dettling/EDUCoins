package org.educoins.core;

import com.google.common.annotations.VisibleForTesting;

import org.educoins.core.p2p.peers.HttpProxyPeerGroup;
import org.educoins.core.store.*;
import org.educoins.core.utils.FormatToScientifc;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class BlockChain implements IBlockListener, ITransactionListener, IPoWListener {

	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
	private static final int IN_SECONDS = 1000;
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final int RESET_BLOCKS_COUNT = 0;

	private final Logger logger = LoggerFactory.getLogger(BlockChain.class);

	private int blockCounter;
	private IBlockReceiver blockReceiverPeerGroup;
	private IBlockReceiver blockReceiverResetMiner;
	private List<IBlockListener> blockListeners;
	private ITransactionReceiver transactionReceiver;
	private ITransactionTransmitter transactionTransmitter;
	private List<ITransactionListener> transactionListeners;
	private List<Transaction> transactions;
	private List<Block> blocks;
	private Wallet wallet;
	private Verification verification;
	private IBlockStore store;

	private String publicKey;

	public BlockChain(IBlockReceiver blockReceiverPeerGroup, IBlockReceiver blockReceiverResetMiner, ITransactionReceiver transactionReceiver,
			ITransactionTransmitter transactionTransmitter, IBlockStore senderBlockStore) {

		this.wallet = new Wallet();
		this.blockListeners = new CopyOnWriteArrayList<>();
		this.blockReceiverPeerGroup = blockReceiverPeerGroup;
		this.blockReceiverPeerGroup.addBlockListener(this);
		this.blockReceiverResetMiner = blockReceiverResetMiner;
		this.blockReceiverResetMiner.addBlockListener(this);
		this.transactionListeners = new ArrayList<>();
		this.transactionReceiver = transactionReceiver;
		this.transactionTransmitter = transactionTransmitter;
		this.transactionReceiver.addTransactionListener(this);
		this.transactions = new ArrayList<>();
		this.verification = new Verification(this.wallet, this);
		this.store = senderBlockStore;

		this.blockCounter = RESET_BLOCKS_COUNT;
	}
	
	public HttpProxyPeerGroup getHttpProxyPeerGroup(){
		return (HttpProxyPeerGroup) this.blockReceiverPeerGroup;
	}
	
	@VisibleForTesting
	public static Sha256Hash calcNewDifficulty(Sha256Hash oldDiff, long currentTime, long allBlocksSinceLastTime) {
		BigDecimal oldDifficulty = new BigDecimal(oldDiff.toBigInteger()).setScale(SCALE_DECIMAL_LENGTH,
				BigDecimal.ROUND_HALF_UP);

		BigDecimal actualBlockTime = BigDecimal.valueOf(currentTime - allBlocksSinceLastTime)
				.setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);

		// New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks /
		// 20160 minutes)
		BigDecimal returnValue = oldDifficulty
				.multiply(actualBlockTime.divide(BigDecimal.valueOf(DESIRED_BLOCK_TIME), BigDecimal.ROUND_HALF_DOWN)
						.setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP));

		System.out.println("+++++++++++++++++++++");
		System.out.println("ActualBlockTime: " + actualBlockTime);
		System.out.println("OldDiff: " + FormatToScientifc.format(oldDifficulty, 1) + " | newDiff: "
				+ FormatToScientifc.format(returnValue, 1));
		System.out.println("+++++++++++++++++++++");

		return Sha256Hash.wrap(returnValue.toBigInteger().toByteArray());
	}

	public @NotNull Block getLatestBlock() throws BlockNotFoundException {
		Block latest = store.getLatest();
		if (latest != null)
			return latest;
		return new Block();
	}

	public @NotNull Block getBlock(Sha256Hash blockHash) throws BlockNotFoundException {
		return store.get(blockHash);
	}

	public @NotNull Collection<Block> getBlocks() throws BlockNotFoundException {
		List<Block> blocks = new ArrayList<>();
		IBlockIterator iterator = store.iterator();
		while (iterator.hasNext()) {
			blocks.add(iterator.next());
		}
		//Includes the genesis block as well.
		blocks.add(this.store.get(blocks.get(blocks.size() -1).getHashPrevBlock()));
		Collections.reverse(blocks);	
		return blocks;
	}

	public @NotNull Collection<Block> getBlocksFrom(Sha256Hash from) throws BlockNotFoundException {
		List<Block> blocks = new ArrayList<>();
		IBlockIterator iterator = store.iterator();

		while (iterator.hasNext()) {
			Block next = iterator.next();
			if (next.hash().equals(from))
				return blocks;
			blocks.add(next);
		}

		Set<Block> blocksFrom = blocks.stream().filter(block -> block.hash().equals(from)).collect(Collectors.toSet());
		if (blocksFrom.size() > 1)
			throw new IllegalStateException("More than one block with the same hash found!");

		if (blocksFrom.size() == 0 && from.equals(iterator.get().hash()))
			throw new BlockNotFoundException(from.getBytes());

		return blocks;
	}

	public @NotNull Collection<Block> getBlockHeaders() throws BlockNotFoundException {
		List<Block> headers = new ArrayList<>();
		IBlockIterator iterator = store.iterator();
		while (iterator.hasNext()) {
			headers.add(iterator.next().getHeader());
		}
		return headers;
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
		synchronized (this) {
			for (IBlockListener blockListener : this.blockListeners) {
				blockListener.blockReceived(newBlock);
			}
		}
	}

	@Override
	public void blockReceived(Block block) {
		logger.info("Received block. Verifying now...");
		
		if (!this.verification.verifyBlock(block)) {
			logger.warn("Verification of block failed: " + block.hash());
			// TODO: cool so?
			return;
		}
		logger.info("Verified Block stored in the BC: " + block.toString());
		this.store.put(block);
		
		Block newBlock = prepareNewBlock(block);
		notifyBlockReceived(newBlock);
		List<Transaction> transactions = block.getTransactions();
		if (transactions != null) {
			logger.info("Found {} transactions", transactions.size());
			for (Transaction transaction : transactions) {
				notifyTransactionReceived(transaction);
			}
		}
		logger.info("Block processed.");
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
		logger.info("Received transaction.");
		switch (transaction.whichTransaction()) {
		case APPROVED:
			if (this.verification.verifyApprovedTransaction(transaction)) {
				this.transactions.add(transaction);
			}
			break;
		case REGULAR:
			if (this.verification.verifyRegularTransaction(transaction)) {
				this.transactions.add(transaction);
			}
			break;
		case REVOKE:
			if (this.verification.verifyRevokeTransaction(transaction)) {
				this.transactions.add(transaction);
			}
			break;
		}
	}

	@Override
	public void foundPoW(Block block) {
		logger.info("Found pow. (Block {})", block.hash().toString());
		this.store.put(block);
		logger.info("Added block to blockStore: " + this.store.getLatest().toString());

		try {
			this.blockReceiverResetMiner.receiveBlocks(null);
			this.blockReceiverPeerGroup.receiveBlocks(getLatestBlock().hash());
		} catch (BlockNotFoundException e) {
			this.blockReceiverResetMiner.receiveBlocks(null);
			this.blockReceiverPeerGroup.receiveBlocks(block.hash());
		}
	}

	public Block prepareNewBlock(Block currentBlock) {
		Block newBlock = new Block();
		// TODO [joeren]: which version?! Temporary take the version of the
		// previous block.
		newBlock.setVersion(currentBlock.getVersion());
		newBlock.setHashPrevBlock(currentBlock.hash());
		// TODO [joeren]: calculate hash merkle root! Temporary take the
		// hash merkle root of the previous block.
		newBlock.setHashMerkleRoot(currentBlock.getHashMerkleRoot());

		newBlock.addTransaction(coinbaseTransaction(currentBlock));
		newBlock.addTransactions(this.transactions);
		this.transactions.clear();

		return retargedBits(newBlock, currentBlock);
	}

	private Transaction coinbaseTransaction(Block currentBlock) {

		// TODO [Vitali] Needs to be changes just for testing.
		if (this.publicKey == null) {
			this.publicKey = this.wallet.getPublicKey();
		}

		// TODO [Vitali] lockingScript procedure has to be established, which
		// fits our needs...
		String lockingScript = publicKey;

		// Input is empty because it is a coinbase transaction.
		int newReward = currentBlock.rewardCalculator();
		Output output = new Output(newReward, publicKey, lockingScript);

		CoinbaseTransaction transaction = new CoinbaseTransaction();
		transaction.addOutput(output);
		return transaction;
	}

	/**
	 * Bitcoin explanation: Mastering Bitcoin 195 Every 2,016 blocks, all nodes
	 * retarget the proof-of-work difficulty. The equation for retargeting
	 * difficulty measures the time it took to find the last 2,016 blocks and
	 * compares that to the expected time of 20,160 minutes.
	 * <p>
	 * New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks /
	 * 20160 minutes)
	 */
	private Block retargedBits(Block newBlock, Block previousBlock) {
		if (this.blockCounter == CHECK_AFTER_BLOCKS) {
			long currentTime = System.currentTimeMillis();
			long allBlocksSinceLastTime = previousBlock.getTime();

			newBlock.setBits(calcNewDifficulty(previousBlock.getBits(), currentTime, allBlocksSinceLastTime));
			newBlock.setTime(currentTime);
			this.blockCounter = RESET_BLOCKS_COUNT;
		} else {
			// The last time stamp since the last retargeting of the difficulty.
			newBlock.setTime(previousBlock.getTime());
			newBlock.setBits(previousBlock.getBits());
		}
		this.blockCounter++;
		return newBlock;
	}

	// TODO [Vitali] Method needs to be deleted as soon as the DB will be
	// introduced.
	public Block getPreviousBlock(Block currentBlock) throws BlockNotFoundException {
		return this.store.get(currentBlock.getHashPrevBlock());
	}

	public Transaction getTransaction(Sha256Hash hash) {
		IBlockIterator it = this.store.iterator();
		while (it.hasNext()) {
			try {
				Block block = it.next();
				Transaction transaction = block.getTransaction(hash);
				if (transaction != null)
					return transaction;
			} catch (BlockNotFoundException ignored) {
			}
		}
		return null;
	}
}
