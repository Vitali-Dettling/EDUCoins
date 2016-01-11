package org.educoins.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.core.transaction.CoinbaseTransaction;
import org.educoins.core.transaction.ITransactionFactory;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.TransactionFactory;
import org.educoins.core.utils.FormatToScientifc;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class BlockChain {

	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
	private static final int IN_SECONDS = 1000;
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final int RESET_BLOCKS_COUNT = 0;

	private final Logger logger = LoggerFactory.getLogger(BlockChain.class);

	private int blockCounter;
	private List<IBlockListener> blockListeners;

	private ITransactionTransmitter transactionTransmitter;
	private List<ITransactionListener> transactionListeners;
	private List<Transaction> transactions;

	private Verification verification;
	private IBlockStore store;

	private IProxyPeerGroup remoteProxies;
	private ITransactionFactory transactionFactory;

	public BlockChain(IProxyPeerGroup remoteProxies, IBlockStore store) {

		this.store = store;
		this.remoteProxies = remoteProxies;

		this.transactionFactory = new TransactionFactory();
		this.blockListeners = new CopyOnWriteArrayList<>();
		this.transactionListeners = new ArrayList<>();
		this.transactions = new ArrayList<>();
		this.verification = new Verification(this);

		this.blockCounter = RESET_BLOCKS_COUNT;
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

	public @NotNull Block getLatestBlock() {
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
		// Includes the genesis block.
		if (!blocks.isEmpty()) {
			Block genesisBlock = this.store.getGenesisBlock();
			blocks.add(genesisBlock);
		}
		Collections.reverse(blocks);

		return blocks;
	}

	public @NotNull Collection<Block> getBlocksFrom(Sha256Hash from) throws BlockNotFoundException {
		List<Block> blocks = new ArrayList<>();
		IBlockIterator iterator = this.store.iterator();

		while (iterator.hasNext()) {
			// TODO Does not return the genesis block.
			Block next = iterator.next();
			blocks.add(next);
			if (next.hash().equals(from)) {
				Collections.reverse(blocks);
				return blocks;
			}
		}

		// Includes the genesis block.
		if (!blocks.isEmpty()) {
			Block genesisBlock = this.store.getGenesisBlock();
			blocks.add(genesisBlock);
		}
		Collections.reverse(blocks);

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

	public void addBlockListener(IBlockListener blockListener) {
		this.blockListeners.add(blockListener);
	}

	public void removeBlockListener(IBlockListener blockListener) {
		this.blockListeners.remove(blockListener);
	}

	public void notifyBlockReceived(Block newBlock) {
		synchronized (this) {
			for (IBlockListener blockListener : this.blockListeners) {
				blockListener.blockListener(newBlock);
			}
		}
	}

	public void verifyReceivedBlock(Block receivedBlock) {
		logger.info("Received block. Verifying now...");

		// Already up to date.
		Block latestStoredBlock = getLatestBlock();
		if (receivedBlock.equals(latestStoredBlock)) {
			logger.info("Blockchain is up to date.");
			return;
		}

		// Check block for validity.
		if (!this.verification.verifyBlock(receivedBlock)) {
			logger.warn("Verification of block failed: " + receivedBlock.toString());
			// Tries as long as the blockchain is up to date.
			Block latestBlock = getLatestBlock();
			System.out.println(latestBlock.hash().toString());
			this.remoteProxies.receiveBlocks(latestBlock.hash());
			return;
		}

		// Store the verified block.
		logger.info("Received Block stored in the BC after verification: " + receivedBlock.toString());
		this.store.put(receivedBlock);
		List<Transaction> transactions = receivedBlock.getTransactions();
		if (transactions != null) {
			logger.info("Found {} transactions", transactions.size());
			for (Transaction transaction : transactions) {
				notifyTransactionReceived(transaction);
			}
		}
		logger.info("Block processed.");
	}

	public void notifyTransactionReceived(Transaction transaction) {
		for (int i = 0; i < this.transactionListeners.size(); i++) {
			ITransactionListener listener = this.transactionListeners.get(i);
			listener.transactionReceived(transaction);
		}
	}

	public void sendTransaction(Transaction transaction) {

		this.transactions.add(transaction);
		// TODO Implementation of the transaction broadcast.
		this.transactionTransmitter.transmitTransaction(transaction);
	}

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

	public Block prepareNewBlock(Block currentBlock, String publicKey) {
		Block newBlock = new Block();
		// TODO [joeren]: which version?! Temporary take the version of the
		// previous block.
		newBlock.setVersion(currentBlock.getVersion());
		newBlock.setHashPrevBlock(currentBlock.hash());
		// TODO [joeren]: calculate hash merkle root! Temporary take the
		// hash merkle root of the previous block.
		newBlock.setHashMerkleRoot(currentBlock.getHashMerkleRoot());

		newBlock.addTransaction(coinbaseTransaction(currentBlock, publicKey));
		newBlock.addTransactions(this.transactions);
		this.transactions.clear();

		return retargedBits(newBlock, currentBlock);
	}

	private Transaction coinbaseTransaction(Block currentBlock, String publicKey) {

		// Input is empty because it is a coinbase transaction.
		int calculatedAmount = currentBlock.rewardCalculator();
		Transaction transaction = this.transactionFactory.generateCoinbasedTransaction(calculatedAmount, publicKey);
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

	public Block getGenesisBlock() {
		try {
			return store.getGenesisBlock();
		} catch (BlockNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void storeBlock(Block block) {
		this.store.put(block);
	}
}
