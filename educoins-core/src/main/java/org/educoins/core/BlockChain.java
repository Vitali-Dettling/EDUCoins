package org.educoins.core;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.educoins.core.Input.EInputUnlockingScript;
import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Deserializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BlockChain implements IBlockListener, ITransactionListener, IPoWListener {

	private static final int COMPARE_EQUAL = 0;
	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
	private static final int IN_SECONDS = 1000;
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final int HEX = 16;
	private static final int RESET_BLOCKS_COUNT = 0;
	private static final int DEFAULT_REWARD = 10;
	private static final int ZERO = 0;
	private static final int NO_COINS = 0;
	private static final int HAS_NO_ENTRIES = 0;
	private static final int ONLY_ONE_COINBASE_TRANSACTION = 1;
	private static final String GENIUSES_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";
	
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
	
	private String publicKey;

	public BlockChain(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter, ITransactionReceiver transactionReceiver, ITransactionTransmitter transactionTransmitter) {
		
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
		if(verifyBlock(block)){
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
		for (ITransactionListener listener : this.transactionListeners) {
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
			if (verifyRegularTransaction(transaction)) {
				this.transactions.add(transaction);
			}
		} else if (type == ETransaction.APPROVED) {
			if (verifyApprovedTransaction(transaction)) {
				this.transactions.add(transaction);
			}
		}
	}
	
	@Override
	public void foundPoW(Block block) {
		this.blockTransmitter.transmitBlock(block);
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

	private boolean verifyBlock(Block toVerifyBlock) {
		
		// 0. If geniuses block return true, because there no other block before.
		if (toVerifyBlock.getHashPrevBlock().equals(GENIUSES_BLOCK)) {
			return true;
		}

		// 1. Find the previous block.
		Block lastBlock = getPreviousBlock(toVerifyBlock);

		// 2. Does the previous block exist?
		if (lastBlock == null) {
			return false;
		}

		// 3. Are the hashes equal of the current block and the previous one?
		byte[] testBlockHash = toVerifyBlock.hash();
		byte[] lastBlockHash = lastBlock.getHashPrevBlock();
		if (ByteArray.compare(testBlockHash, lastBlockHash) == COMPARE_EQUAL) {
			return false;
		}
		
		//4. At least one transaction has to be in the block, namely the coinbase transaction.
		if(toVerifyBlock.getTransactions().size() <=  HAS_NO_ENTRIES){
			return false;
		}
		
		//5. Verification of all transactions in a block.
		boolean isTransactionValid = true;
		List<Transaction> transactions = toVerifyBlock.getTransactions();
	    for (Transaction transaction : transactions) {
	    	
			//5.1 Check for transaction type.
			if(transaction.whichTransaction() == ETransaction.COINBASE){
				isTransactionValid = verifyCoinbaseTransaction(transaction, toVerifyBlock);
			}
			else if(transaction.whichTransaction() == ETransaction.REGULAR){
				isTransactionValid = verifyRegularTransaction(transaction);
			}
			else if(transaction.whichTransaction() == ETransaction.APPROVED){
				isTransactionValid = verifyApprovedTransaction(transaction);
			}
			
			//As soon as a transaction is not valid, the loop will be cancelled.
			if(!isTransactionValid){
				return false;
			}
		}
		
		// TODO[Vitali] Überlegen ob weitere Test von nöten wären???

		return true;

	}
	
	private boolean verifyApprovedTransaction(Transaction transaction){
		
		//TODO [Vitali] Find out whether all checks are included? 
		
		// After "Bildungsnachweise als Digitale Währung - eine Anwendung der Block-Chain-Technologie" p. 37f

		// Case 1:
		// TODO [joeren]: Syntax has not to be verified in first step, already done by the deserializer
		
		List<Input> inputs = transaction.getInputs();
		List<Approval> approvals = transaction.getApprovals();
		
		if (approvals == null) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: inputs is null");
			return false;
		}

		int sumInputsAmount = 0;
		int sumApprovalAmount = 0;
		
		// Case 4:
		for (Input input : inputs) {
			int amount = input.getAmount();
			if (amount <= NO_COINS) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyRegularTransaction: input amounts is negative or zero");
				return false;
			}
			// sum up for case 5
			sumInputsAmount += amount;
		}
				
		for(Approval approval : approvals){
			if(approval.getAmount() <= NO_COINS){
				System.out.println("DEBUG: verifyApprovedTransaction: approved amound is 0");
				return false;
			}
			
			int amount = approval.getAmount();
			if (amount <= NO_COINS) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyRegularTransaction: output amount is negative or zero");
				return false;
			}
			// sum up for case 5
			sumApprovalAmount += amount;
		}
		
		// Case 5:
		// TODO [joeren]: implementation of approval-exception
		if (sumApprovalAmount > sumInputsAmount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: more output than input");
			return false;
		}
		
		//Case 13:
		//TODO [Vitali] Implement the check for the lock script as soon as the Revoke class was introduced. 
		//Till then there is no use in implementing it. 
		//For the time being it only checked that Locking Script is not empty.
		for(Approval approval : approvals){
			String lockingScript = approval.getLockingScript();
			if(lockingScript.isEmpty()){
				System.out.println("DEBUG: verifyRegularTransaction: locking script is empty.");
				return false;
			}
		}
		
		
		//TODO [Vitali] Implement rest of the verification, if some.
		
		return true;
		
		
	}
	
	private boolean verifyCoinbaseTransaction(Transaction transaction, Block toVerifyBlock){
		
		//TODO [Vitali] Find out whether all checks are included? 
		
		// After "Bildungsnachweise als Digitale Währung - eine Anwendung der Block-Chain-Technologie" p. 37f

		// Case 1:
		// TODO [joeren]: Syntax has not to be verified in first step, already done by the deserializer

		List<Output> coinBases = transaction.getOutputs();
		
		if(coinBases == null){
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyCoinbaseTransaction: output is null");
			return false;
		}
		
		if(coinBases.size() != ONLY_ONE_COINBASE_TRANSACTION){
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyCoinbaseTransaction: More then one coinbase transaction.");
			return false;
		}
		
		Output coinBase = coinBases.iterator().next();
		
		int currentReward = coinBase.getAmount();
		Block previousBlock = getPreviousBlock(toVerifyBlock);
		int trueReward = rewardCalculator(previousBlock);
		if(trueReward != currentReward){
			System.out.println("DEBUG: verifyCoinbaseTransaction: reward cannot be zero.");
			return false;
		}
		
		//TODO [Vitali] Implement rest of the verification, if some.
		
		return true;
		
	}
	
	private boolean verifyRegularTransaction(Transaction transaction) {

		// After "Bildungsnachweise als Digitale Währung - eine Anwendung der Block-Chain-Technologie" p. 37f

		// Case 1:
		// TODO [joeren]: Syntax has not to be verified in first step, already done by the deserializer

		List<Input> inputs = transaction.getInputs();

		if (inputs == null) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: inputs is null");
			return false;
		}

		int realInputsCount = inputs.size();

		if (realInputsCount == ZERO) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: realInputsCount is 0");
			return false;
		}

		int inputsCount = transaction.getInputsCount();

		if (realInputsCount != inputsCount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: realInputsCount does not match inputsCount");
			return false;
		}

		List<Output> outputs = transaction.getOutputs();

		if (outputs == null) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: outputs is null");
			return false;
		}

		int realOutputsCount = outputs.size();

		if (realOutputsCount == ZERO) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: realOutputsCount is 0");
			return false;
		}

		int outputsCount = transaction.getOutputsCount();

		if (realOutputsCount != outputsCount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: realOutputsCount does not match outputsCount");
			return false;
		}
		
		int sumInputsAmount = NO_COINS;
		int sumOutputsAmount = NO_COINS;
		
		// Case 4:
		for (Input input : inputs) {
			int amount = input.getAmount();
			if (amount <= NO_COINS) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyRegularTransaction: input amounts is negative or zero");
				return false;
			}
			// sum up for case 5
			sumInputsAmount += amount;
		}
		for (Output output : outputs) {
			int amount = output.getAmount();
			if (amount <= NO_COINS) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyRegularTransaction: output amount is negative or zero");
				return false;
			}
			// sum up for case 5
			sumOutputsAmount += amount;
		}
		
		// Case 5:
		// TODO [joeren]: implementation of approval-exception
		if (sumOutputsAmount > sumInputsAmount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyRegularTransaction: more output than input");
			return false;
		}
		
		//Case 13:
		//TODO [Vitali] The check is current done with the ECDSA class but actually that should be done through the script language.
		//Currently it check just whether the signature corresponds with one public key in the wallet file. 
		byte[] signature = null;
		String hashedTransaction = ByteArray.convertToString(transaction.hash(), HEX);
		for(Input input : transaction.getInputs()){
			
			signature = input.getUnlockingScript(EInputUnlockingScript.SIGNATURE);
				
			if(this.wallet.checkSignature(hashedTransaction, signature)){
				System.out.println("INFO: verifyRegularTransaction: Signature is correct.");
				break; 
			}
					
		}
		
		//TODO [Vitali] Implement rest of the verification, if some.
		
		return true;
	}
	
	
	//TODO [Vitali] Method needs to be deleted as soon as the DB will be introduced.
	private Block getPreviousBlock(Block currentBlock) {
		try {

			byte[] lastBlockName = currentBlock.getHashPrevBlock();

			// TODO[Vitali] Der remoteStorage String ist nur für den Prototypen, sollte geändert werden sobald eine
			// levelDB eingeführt wird!!!
			String remoteStoragePath = System.getProperty("user.home") + File.separator + "documents" + File.separator
					+ "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";

			return Deserializer.deserialize(remoteStoragePath, ByteArray.convertToString(lastBlockName));
			
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			System.out.println("ERROR: Class Verifier: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}



}
