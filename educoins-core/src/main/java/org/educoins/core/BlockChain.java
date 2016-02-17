package org.educoins.core;

import com.google.common.annotations.VisibleForTesting;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.store.*;
import org.educoins.core.transaction.*;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.educoins.core.utils.FormatToScientifc;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class BlockChain implements IBlockListener {

	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 30;
	private static final int IN_SECONDS = 1000;
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final int RESET_BLOCKS_COUNT = 0;

	private static final BigDecimal desiredBlockTime = BigDecimal.valueOf(DESIRED_BLOCK_TIME)
			.setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);

	private final Logger logger = LoggerFactory.getLogger(BlockChain.class);

	private int blockCounter;
	private List<IBlockListener> blockListeners;

	private ITransactionTransmitter transactionTransmitters;
	private List<ITransactionListener> transactionListeners;
	private List<Transaction> transactions;

	private Verification verification;
	private IBlockStore store;

	private IProxyPeerGroup remoteProxies;
	private ITransactionFactory transactionFactory;

	public BlockChain(IProxyPeerGroup remoteProxies, IBlockStore store) {

		this.store = store;
		this.remoteProxies = remoteProxies;
		this.transactionTransmitters = remoteProxies;

		this.transactionFactory = new TransactionFactory();
		this.blockListeners = new CopyOnWriteArrayList<>();
		this.transactionListeners = new ArrayList<>();
		this.transactions = new ArrayList<>();
		this.verification = new Verification(this);

		this.blockCounter = RESET_BLOCKS_COUNT;
	}

	/**
	 * Formula: New Difficulty = Old Difficulty * (Actual Time of Last 10
	 * Blocks/ 30 minutes)
	 */
	@VisibleForTesting
	public static Sha256Hash calcNewDifficulty(Sha256Hash oldDiff, long currentTime, long allBlocksSinceLastTime) {

		BigDecimal oldDifficulty = new BigDecimal(oldDiff.toBigInteger()).setScale(SCALE_DECIMAL_LENGTH,
				BigDecimal.ROUND_HALF_UP);

		BigDecimal actualBlockTime = BigDecimal.valueOf(currentTime - allBlocksSinceLastTime);
		BigDecimal dif = actualBlockTime.divide(desiredBlockTime, SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);
		BigDecimal returnValue = oldDifficulty.multiply(dif.setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP));

		System.out.println("+++++++++++++++++++++");
		System.out.println("ActualBlockTime: " + FormatToScientifc.format(actualBlockTime, 1));
		System.out.println("OldDiff: " + FormatToScientifc.format(oldDifficulty, 1) + " | newDiff: "
				+ FormatToScientifc.format(returnValue, 1));
		System.out.println("+++++++++++++++++++++");

		return Sha256Hash.wrap(returnValue.toBigInteger().toByteArray());
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

	public void notifyBlockReceivedFromNode(@NotNull Block block) {
		logger.info("Received Block from foreign node.");
		if (!contains(block) && validateBlock(block))
			notifyBlockReceived(block);
		else
			logger.info("Block should not be recognized any further.");
	}

	// TODO: Rename and redesign. This method does way more than just verify.
	public void verifyReceivedBlock(Block receivedBlock) {
		if (storeBlock(receivedBlock))
			return;

		// Store the verified block.
		logger.info("Received Block stored in the BC after verification: " + receivedBlock.toString());

		this.store.put(receivedBlock);

	}

	/**
	 * Checks if the Block is valid and stores it (if valid).
	 *
	 * @return <code>true</code> if valid and stored, else <code>false</code>.
	 */
	public boolean storeBlock(Block block) {
		if (!validateBlock(block))
			return false;
		store.put(block);
		logger.info("Block stored. hash {}", block.hash());
		return true;
	}

	private boolean validateBlock(Block block) {
		logger.info("Verifying Block. hash: {}", block.hash());

		// Check block for validity.
		if (!this.verification.verifyBlock(block)) {
			logger.warn("Verification of block failed. hash: {}, block: {}", block.hash(), block.toString());
			return false;
		}
		return true;
	}

	public void notifyTransactionReceived(Transaction transaction) {
		for (int i = 0; i < this.transactionListeners.size(); i++) {
			ITransactionListener listener = this.transactionListeners.get(i);
			listener.transactionReceived(transaction);
		}
	}

	public void sendTransaction(Transaction transaction) {
		logger.info("Transaction of type {} submitted.", transaction.whichTransaction());
		this.transactions.add(transaction);
		this.transactionTransmitters.transmitTransaction(transaction);
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
				logger.error("could not find block in Chain, very strange.");
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

	@Override
	public void blockListener(Block block) {
		boolean isVerified = this.verification.verifyBlock(block);
		if (isVerified) {
			store.put(block);
			// TODO: Open Transactions have to be removed, if they are inside
			// the new block
		}
	}

	public ETransaction approvalValide(String stillApproved) {
		try {
			for (Block block : this.getBlocks()) {
				for (Transaction tx : block.getTransactions()) {
					if(tx.hash().toString().equals(stillApproved)){
						if(tx.whichTransaction() == ETransaction.APPROVED){
							return this.verification.approvalValide(stillApproved);
						}
					}
				}
			}
		} catch (BlockNotFoundException e) {
			logger.error("While checking the approved transaction against the revoke transaction. ");
		}
		return null;
	}

	public boolean contains(Block block) {
		return store.contains(block);
	}

}
