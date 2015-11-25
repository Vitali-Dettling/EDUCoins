package org.educoins.core;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.IO;
import org.educoins.core.utils.IO.EPath;
import org.educoins.core.store.ITransactionIterator;
import org.educoins.core.utils.Sha256Hash;

import educoins.core.utils.BlockStoreFactory;

public class BlockChain implements IBlockListener, ITransactionListener, IPoWListener {

	private static final int CHECK_AFTER_BLOCKS = 100;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
	private static final int IN_SECONDS = 1000;
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final int RESET_BLOCKS_COUNT = 0;

	private int blockCounter;
	private IBlockReceiver blockReceiver;
	private List<IBlockListener> blockListeners;
	private ITransactionReceiver transactionReceiver;
	private ITransactionTransmitter transactionTransmitter;
	private List<ITransactionListener> transactionListeners;
	private List<Transaction> transactions;
	private Wallet wallet;
	private Block newBlock;
	private Verification verification;
	private IBlockStore store;
	private List<Gateway> externGateways;
	private Gateway myGateway;

	private String publicKey;

	public BlockChain(IBlockReceiver blockReceiver, ITransactionReceiver transactionReceiver, ITransactionTransmitter transactionTransmitter, IBlockStore senderBlockStore) {
		
				
		this.wallet = new Wallet(IO.getDefaultFileLocation(EPath.DEMO, EPath.WALLET));
		this.blockListeners = new CopyOnWriteArrayList<>();
		this.blockReceiver = blockReceiver;
		this.blockReceiver.addBlockListener(this);
		this.transactionListeners = new ArrayList<>();
		this.transactionReceiver = transactionReceiver;
		this.transactionTransmitter = transactionTransmitter;
		this.transactionReceiver.addTransactionListener(this);
		this.transactions = new ArrayList<>();
		this.verification = new Verification(this.wallet, this);
		this.store = senderBlockStore;
		this.externGateways = new ArrayList<Gateway>();
		this.myGateway = new Gateway();

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
		synchronized (this) {
			for (IBlockListener blockListener : blockListeners) {
				blockListener.blockReceived(newBlock);
			}
		}
	}

	@Override
	public void blockReceived(Block block) {
		if (this.verification.verifyBlock(block)) {
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
		} else if (type == ETransaction.GATEWAY) {
			if (this.verification.verifyGateway(transaction)) {
				this.externGateways.add(this.myGateway);
			}
		} else if (type == ETransaction.GATE) {
			if (this.verification.verifyGate(transaction)) {
				createGateway(transaction);
			}
		}
	}

	@Override
	public void foundPoW(Block block) {
		this.store.put(block);
		this.blockReceiver.receiveBlocks();
	}

	public Block prepareNewBlock(Block currentBlock) {
		this.newBlock = new Block();
		// TODO [joeren]: which version?! Temporary take the version of the
		// previous block.
		this.newBlock.setVersion(currentBlock.getVersion());
		this.newBlock.setHashPrevBlock(currentBlock.hash());
		// TODO [joeren]: calculate hash merkle root! Temporary take the
		// hash merkle root of the previous block.
		this.newBlock.setHashMerkleRoot(currentBlock.getHashMerkleRoot());

		this.newBlock.addTransaction(coinbaseTransaction(currentBlock));
		this.newBlock.addTransactions(this.transactions);
		this.transactions.clear();

		if (!this.externGateways.isEmpty()) {
			this.newBlock.addAllGateways(this.externGateways);
		}

		return retargedBits(currentBlock);
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
		Output output = new Output(newReward, Sha256Hash.wrap(publicKey), Sha256Hash.wrap(lockingScript));

		CoinbaseTransaction transaction = new CoinbaseTransaction();
		transaction.addOutput(output);
		return transaction;
	}

	/**
	 * Bitcoin explanation: Mastering Bitcoin 195 Every 2,016 blocks, all nodes
	 * retarget the proof-of-work difficulty. The equation for retargeting
	 * difficulty measures the time it took to find the last 2,016 blocks and
	 * compares that to the expected time of 20,160 minutes.
	 * 
	 * New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks /
	 * 20160 minutes)
	 */
	private Block retargedBits(Block previousBlock) {

		if (this.blockCounter == CHECK_AFTER_BLOCKS) {
			long currentTime = System.currentTimeMillis();
			long allBlocksSinceLastTime = previousBlock.getTime();
			BigDecimal oldDifficulty = new BigDecimal(previousBlock.getBits().toBigInteger())
					.setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);
			BigDecimal actualBlockTime = BigDecimal.valueOf(currentTime - allBlocksSinceLastTime)
					.setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);

			// New Difficulty = Old Difficulty * (Actual Time of Last 2016
			// Blocks / 20160 minutes)
			BigDecimal newDifficulty = oldDifficulty
					.multiply(actualBlockTime.divide(BigDecimal.valueOf(DESIRED_BLOCK_TIME), BigDecimal.ROUND_HALF_DOWN)
							.setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP));

			this.newBlock.setBits(Sha256Hash.wrap(newDifficulty.toBigInteger().toByteArray()));
			this.newBlock.setTime(currentTime);
			this.blockCounter = RESET_BLOCKS_COUNT;
		} else {
			// The last time stamp since the last retargeting of the difficulty.
			this.newBlock.setTime(previousBlock.getTime());
			this.newBlock.setBits(previousBlock.getBits());
		}
		this.blockCounter++;
		return this.newBlock;
	}

	public Block getPreviousBlock(Block currentBlock) {
		return this.store.get(currentBlock.hash().getBytes());
	}

	private void createGateway(Transaction transaction) {

		// TODO [Vitali] How to find out about all gateways?
		Gate gate = transaction.getGate();
		if (this.verification.verifyGate(transaction)) {
			this.myGateway.addGate(gate);
			// When all signatures of the other gateways have been collected.
			// Then it will continue transmit the created new gateway.
			if (allGatesSigned()) {
				this.externGateways.add(this.myGateway);
				transaction.setGateways(this.externGateways);
				this.sendTransaction(transaction);
			}
		}
	}

	private boolean allGatesSigned() {

		// TODO Delete
		System.out.println("allGatesSigned: " + this.myGateway.toString());

		final int notGatewayExist = 0;

		int optainedGatewaysInBC = findAllGateway();
		int precent = (int) (optainedGatewaysInBC * 0.8);

		// TODO[Vitali] Write a test.
		if (optainedGatewaysInBC == precent) {
			return true;
		} else if (optainedGatewaysInBC == notGatewayExist) {
			return true;
		}

		return false;
	}

	private int findAllGateway() {

		int countGateways = 0;
		IBlockIterator iterator = this.store.blockIterator();

		while (iterator.hasNext()) {
			Block block = iterator.next();
			for (Transaction transaction : block.getTransactions()) {
				if (transaction.getGatewaysCount() == 0) {
					countGateways = transaction.getGatewaysCount();
				}
			}

		}

		return countGateways;
	}

}
