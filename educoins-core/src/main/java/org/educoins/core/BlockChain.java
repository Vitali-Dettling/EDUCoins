package org.educoins.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Deserializer;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class BlockChain implements IBlockListener {

	private static final int TRUE = 0;
	private static final int CHECK_AFTER_BLOCKS = 10;
	private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
	private static final int IN_SECONDS = 1000;
	//TODO aus irgend einem Grund funktioniert die Desired Time nicht? Um so höcher diese ist um so unwahrscheinlicher kalibriert sich die Difficulty???
	private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
	private static final int SCALE_DECIMAL_LENGTH = 100;
	private static final int HEX = 16;
	private static final int RESET_BLOCKS_COUNT = 0;
	private static final int DEFAULT_REWARD = 10;
	private static final int ZERO = 0;
	private static final String GENIUSES_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";
	
	private int blockCounter;
	private IBlockReceiver blockReceiver;
	private IBlockTransmitter blockTransmitter;
	private List<IBlockListener> blockListeners;
	private Wallet wallet;
	private Block previousBlock;
	private Block newBlock;

	public BlockChain(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter) {
		
		this.wallet = new Wallet(this);
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
		Output output = new Output(rewardCalculator(), publicKey, lockingScript);

		RegularTransaction transaction = new RegularTransaction(); 
		transaction.addOutput(output);
		return transaction;
	}
	
	
	private int rewardCalculator(){
		
		int newReward = ZERO;
		int lastCoinbaseReward =  this.previousBlock.getLastCoinbaseReword();
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
	//TODO [Vitali] Einigen ob Bits oder Difficulty, damit es einheitlich bleibt!!!
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
	
	
	
	
	
	
	public boolean verifyBlock(Block testBlock) {

		// 0. If geniuses block return true, because there no other block before.
		if (testBlock.getHashPrevBlock().equals(GENIUSES_BLOCK)) {
			return true;
		}

		// 1. Find the previous block.
		Block lastBlock = getPreviousBlock(testBlock);

		// 2. Does the previous block exist?
		if (lastBlock == null) {
			return false;
		}

		// 3. Are the hashes equal of the current block and the previous one?
		byte[] testBlockHash = testBlock.hash();
		byte[] lastBlockHash = lastBlock.getHashPrevBlock().getBytes();
		if (ByteArray.compare(testBlockHash, lastBlockHash) == TRUE) {
			return false;
		}
		
		//4. Verification of all transactions in a block.
		List<Transaction> transactions = testBlock.getTransactions();
		if (transactions != null) {
			for (Transaction transaction : transactions) {
				verifyTransaction(transaction);
			}
		}

		// TODO[Vitali] Überlegen ob weitere Test von nöten wären???

		return true;

	}

	
	
	
	
	
	public boolean verifyTransaction(Transaction transaction) {

		// After "Bildungsnachweise als Digitale Währung - eine Anwendung der Block-Chain-Technologie" p. 37f

		// Case 1:
		// TODO [joeren]: Syntax has not to be verified in first step, already done by the deserializer

		// Case 2:
		// TODO [joeren]: implementation of approval-exception
		List<Input> inputs = transaction.getInputs();

		if (inputs == null) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: inputs is null");
			return false;
		}

		int realInputsCount = inputs.size();

		if (realInputsCount == 0) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: realInputsCount is 0");
			return false;
		}

		int inputsCount = transaction.getInputsCount();

		if (realInputsCount != inputsCount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: realInputsCount does not match inputsCount");
			return false;
		}

		List<Output> outputs = transaction.getOutputs();

		if (outputs == null) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: outputs is null");
			return false;
		}

		int realOutputsCount = outputs.size();

		if (realOutputsCount == 0) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: realOutputsCount is 0");
			return false;
		}

		int outputsCount = transaction.getOutputsCount();

		if (realOutputsCount != outputsCount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: realOutputsCount does not match outputsCount");
			return false;
		}
		
		int sumInputsAmount = 0;
		int sumOutputsAmount = 0;
		
		// Case 4:
		for (Input input : inputs) {
			int amount = input.getAmount();
			if (amount <= 0) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyTransaction: input amounts is negative or zero");
				return false;
			}
			// sum up for case 5
			sumInputsAmount += amount;
		}
		for (Output output : outputs) {
			int amount = output.getAmount();
			if (amount <= 0) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyTransaction: output amount is negative or zero");
				return false;
			}
			// sum up for case 5
			sumOutputsAmount += amount;
		}
		
		// Case 5:
		// TODO [joeren]: implementation of approval-exception
		if (sumOutputsAmount > sumInputsAmount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: more output than input");
			return false;
		}

		return true;

	}

	private Block getPreviousBlock(Block testblock) {
		try {

			String lastBlockName = testblock.getHashPrevBlock();

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
