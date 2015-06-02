package org.educoins.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Input.EInputUnlockingScriptSeperator;
import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Deserializer;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class BlockChain implements IBlockListener {

	private static final int TRUE = 0;
	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
	private static final int IN_SECONDS = 1000;
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final int HEX = 16;
	private static final int RESET_BLOCKS_COUNT = 0;
	private static final int DEFAULT_REWARD = 10;
	private static final int ZERO = 0;
	private static final int NO_COINS_APPROVED = 0;
	private static final int HAS_ENTRIES = 0;
	private static final int HAS_NO_ENTRIES = 0;
	private static final int ONLY_ONE_COINBASE_TRANSACTION = 1;
	private static final String GENIUSES_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";
	
	private int blockCounter;
	private IBlockReceiver blockReceiver;
	private IBlockTransmitter blockTransmitter;
	private List<IBlockListener> blockListeners;
	private Wallet wallet;
	private Block previousBlock;
	private Block newBlock;

	public BlockChain(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter) {
		
		this.wallet = new Wallet();
		this.blockListeners = new ArrayList<>();
		this.blockReceiver = blockReceiver;
		this.blockTransmitter = blockTransmitter;
		this.blockReceiver.addBlockListener(this);	
	
		this.blockCounter = RESET_BLOCKS_COUNT;
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
		}
	}
	
	public void transmitBlock(Block block){
		this.blockTransmitter.transmitBlock(block);
	}
	
	public Block prepareNewBlock(Block previousBlock) {
		
		this.previousBlock = previousBlock;
		this.newBlock = new Block();
		// TODO [joeren]: which version?! Temporary take the version of the
		// previous block.
		this.newBlock.setVersion(this.previousBlock.getVersion());
		this.newBlock.setHashPrevBlock(ByteArray.convertToString(this.previousBlock.hash(), 16));
		// TODO [joeren]: calculate hash merkle root! Temporary take the
		// hash merkle root of the previous block.
		this.newBlock.setHashMerkleRoot(this.previousBlock.getHashMerkleRoot());
		
		this.newBlock.addTransaction(coinbaseTransaction());
		
		return retargedBits();
	}
	
	
	private Transaction coinbaseTransaction() {

		String publicKey = this.wallet.getPublicKey();
		
		//TODO [Vitali] lockingScript procedure has to be established, which fits our needs...
		String lockingScript = EScripts.DUB.toString() + " " +
							   EScripts.HASH160.toString() + " " +
							   publicKey + " " +//TODO[Vitali] Modify that it can be changed on or more addresses???
							   EScripts.EQUALVERIFY.toString() + " " +
							   EScripts.CHECKSIG.toString();
		
		//Input is empty because it is a coinbase transaction.
		Output output = new Output(rewardCalculator(this.previousBlock), publicKey, lockingScript);

		RegularTransaction transaction = new RegularTransaction(); 
		transaction.addOutput(output);
		return transaction;
	}
	
	
	private int rewardCalculator(Block lastBlock){
		
		int newReward = ZERO;
		int lastCoinbaseReward =  lastBlock.getLastCoinbaseReword();
		int lastApprovedEDUCoins = findAllApprovedEDUCoins();
		
		//TODO[Vitali] Einen besseren mathematischen Algorithmus ausdengen, um die ausschütung zu bestimmen!!!
		if(lastCoinbaseReward == lastApprovedEDUCoins){
			newReward = lastCoinbaseReward;
		}else if(lastCoinbaseReward > lastApprovedEDUCoins){
			newReward = lastApprovedEDUCoins;
		}else if(lastCoinbaseReward < lastApprovedEDUCoins){
			newReward = lastCoinbaseReward;
		}
		
		//Especially at the beginning, if no EDUCoins had been approved that the miner would still get some reward.
		if(newReward == ZERO){
			newReward = DEFAULT_REWARD;
		}

		return newReward;
	}
	
	
	private int findAllApprovedEDUCoins(){
		
		int latestApprovedEDUCoins = ZERO;
		List<Transaction> latestTransactions = this.previousBlock.getTransactions();
		
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
	 * equation for retargeding difficulty measures the time it took to find the
	 * last 2,016 blocks and compares that to the expected time of 20,160 minutes.
	 * 
	 * New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks / 20160 minutes)
	 * */
	private Block retargedBits() {
		
		if(this.blockCounter == CHECK_AFTER_BLOCKS){
			long currentTime = System.currentTimeMillis();
			long allBlocksSinceLastTime = this.previousBlock.getTime();
			BigDecimal oldDifficulty = new BigDecimal(new BigInteger(this.previousBlock.getBits(), HEX)).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);
			BigDecimal actualBlockTime = BigDecimal.valueOf(currentTime - allBlocksSinceLastTime).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);	
			
			// New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks / 20160 minutes)
			BigDecimal newDifficulty = oldDifficulty.multiply(actualBlockTime.divide(BigDecimal.valueOf(DESIRED_BLOCK_TIME), BigDecimal.ROUND_HALF_DOWN).setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP));
			
			this.newBlock.setBits(newDifficulty.toBigInteger().toString(HEX));			
			this.newBlock.setTime(currentTime);
			this.blockCounter = RESET_BLOCKS_COUNT;
		}
		else{
			//The last time stamp since the last retargeting of the difficulty. 
			this.newBlock.setTime(this.previousBlock.getTime());	
			this.newBlock.setBits(this.previousBlock.getBits());	
		}
		this.blockCounter++;
		return this.newBlock;
	}
	
	
	
	
	
	
	
	
	public boolean verifyBlock(Block toVerifyBlock) {

		// 0. If geniuses block return true, because there no other block before.
		if (!toVerifyBlock.getHashPrevBlock().equals(GENIUSES_BLOCK)) {
			return false;
		}

		// 1. Find the previous block.
		Block lastBlock = getPreviousBlock(toVerifyBlock);

		// 2. Does the previous block exist?
		if (lastBlock == null) {
			return false;
		}

		// 3. Are the hashes equal of the current block and the previous one?
		byte[] testBlockHash = toVerifyBlock.hash();
		byte[] lastBlockHash = lastBlock.getHashPrevBlock().getBytes();
		if (ByteArray.compare(testBlockHash, lastBlockHash) == TRUE) {
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
				isTransactionValid = verifyCoinbaseTransaction(transaction);
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
	

	//TODO[Vitali] Change to verify input, output or approved, because the approvals are all the same.
	
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
		
		// Case 4:
		int sumInputsAmount = 0;
		for (Input input : inputs) {
			int amount = input.getAmount();
			if (amount <= 0) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyRegularTransaction: input amounts is negative or zero");
				return false;
			}
			// sum up for case 5
			sumInputsAmount += amount;
		}
		
		
		for(Approval approval : approvals){
			if(approval.getAmount() <= NO_COINS_APPROVED){
				System.out.println("DEBUG: verifyApprovedTransaction: approved amound is 0");
				return false;
			}
			
			if(approval.getHashPrevOutput() != null){
				System.out.println("DEBUG: verifyApprovedTransaction: No previos output allowed.");
				return false;
			}
			
		}
		
		
		//TODO [Vitali] Implement rest of the verification, if some.
		//TODO [Vitali] Implement rest of the verification, if some.
		//TODO [Vitali] Implement rest of the verification, if some.
		//TODO [Vitali] Implement rest of the verification, if some.
		
		return true;
		
		
	}
	
	private boolean verifyCoinbaseTransaction(Transaction transaction){
		
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
		
		if(coinBases.size() == ONLY_ONE_COINBASE_TRANSACTION){
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyCoinbaseTransaction: More then one coinbase transaction.");
			return false;
		}
		
		Output coinBase = coinBases.iterator().next();
		
		int currentReward = coinBase.getAmount();
		Block previousBlock = getPreviousBlock(this.previousBlock);
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

		if (realInputsCount == 0) {
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

		if (realOutputsCount == 0) {
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
		
		int sumInputsAmount = 0;
		int sumOutputsAmount = 0;
		
		// Case 4:
		for (Input input : inputs) {
			int amount = input.getAmount();
			if (amount <= 0) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyRegularTransaction: input amounts is negative or zero");
				return false;
			}
			// sum up for case 5
			sumInputsAmount += amount;
		}
		for (Output output : outputs) {
			int amount = output.getAmount();
			if (amount <= 0) {
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
		//TODO [Vitali] The check is current done with the ECDSA class but actually that should be done through the script algorithm.
		byte[] signature = null;
		String hashedTransaction = transaction.hash().toString();
		for(Input input : transaction.getInputs()){
			signature = input.getUnlockingScript(EInputUnlockingScriptSeperator.SIGNATURE);
					
			if(!this.wallet.checkSignature(hashedTransaction, signature)){
				return false;
			}
					
		}
		
		//TODO [Vitali] Implement rest of the verification, if some.
		
		return true;
	}
	
	
	
	
	
	
	
//TODO[Vitali] Kann das funktionieren??? -> Jören fragen???
	private Block getPreviousBlock(Block currentBlock) {
		try {

			String lastBlockName = currentBlock.getHashPrevBlock();

			// TODO[Vitali] Der remoteStorage String ist nur für den Prototypen, sollte geändert werden sobal eine
			// levelDB eingeführt wird!!!
			String remoteStoragePath = System.getProperty("user.home") + File.separator + "documents" + File.separator
					+ "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";

			return Deserializer.deserialize(remoteStoragePath, lastBlockName);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			System.out.println("ERROR: Class Verifier: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}



}
