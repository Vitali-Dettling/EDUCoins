package org.educoins.core;

import org.educoins.core.Input.EInputUnlockingScript;
import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.utils.BinaryTree;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;

import java.util.List;

public class Verification {
	
	private static final int TRUE = 0;
	private static final int ZERO = 0;
	private static final int NO_COINS = 0;
	private static final int HAS_NO_ENTRIES = 0;
	private static final String GENESIS_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";
	
	private Wallet wallet;
	private BlockChain blockChain;
	
	public Verification(Wallet wallet, BlockChain blockChain){
		this.blockChain = blockChain;
		this.wallet = wallet;	
	}
		
	public boolean verifyBlock(Block toVerifyBlock) {
		
		if(toVerifyBlock == null){
			throw new NullPointerException("Block is null.");	
		}
		
		// 0. If geniuses block return true, because there no other block before.
		if (toVerifyBlock.getHashPrevBlock().equals(Sha256Hash.wrap(GENESIS_BLOCK))) {
			return true;
		}

		// 1. Find the previous block.
		//TODO: Check naming of lastBlock / getPreviousBlock()
		Block lastBlock = this.blockChain.getPreviousBlock(toVerifyBlock);
		
		// 2. Does the previous block exist?
		if (lastBlock == null) {
			System.out.println("DEBUG: verifyBlock: lastBlock is null");
			return false;
		}

		// 3. Are the hashes equal of the current block and the previous one?
		if (toVerifyBlock.hash().compareTo(lastBlock.getHashPrevBlock()) == TRUE) {
			System.out.println("DEBUG: verifyBlock: last block is equal to block");
			return false;
		}

		//4. At least one transaction has to be in the block, namely the coinbase transaction.
		if(toVerifyBlock.getTransactions().size() <=  HAS_NO_ENTRIES){
			System.out.println("DEBUG: verifyBlock: no transactions");
			return false;
		}
		
		//5. Verification of all transactions in a block.
		boolean isTransactionValid = false;
		List<Transaction> transactions = toVerifyBlock.getTransactions();
	    for (Transaction transaction : transactions) {
			//5.1 Check for transaction type.
			switch (transaction.whichTransaction()) {
				case APPROVED:
					isTransactionValid = verifyApprovedTransaction(transaction);
					break;
				case COINBASE:
					isTransactionValid = verifyCoinbaseTransaction(transaction, toVerifyBlock);
					break;
				case REGULAR:
					isTransactionValid = verifyRegularTransaction(transaction);
					break;
				case REVOKE:
					isTransactionValid = verifyRevokeTransaction(transaction);
					break;
			}
			
			//As soon as a transaction is not valid, the loop will be cancelled.
			if(!isTransactionValid){
				return false;
			}
		}
		
	    if (!verifyMerkle(toVerifyBlock))
	    {
			System.out.println("DEBUG: verifyBlock: verfication of merkle root failed");
			return false;
	    }
	    
		// TODO[Vitali] Überlegen ob weitere Test von nöten wären???
	    // TODO maxBlockSIze: size_in_bytes < MAX_BLOCK_SIZE
	    
	    
		return true;

	}
	
	public boolean verifyApprovedTransaction(Transaction transaction){
		
		//TODO [Vitali] Find out whether all checks are included? 
		
		// After "Bildungsnachweise als Digitale Währung - eine Anwendung der Block-Chain-Technologie" p. 37f

		// Case 1:
		// TODO [joeren]: Syntax has not to be verified in first step, already done by the deserializer
		
		List<Input> inputs = transaction.getInputs();
		List<Approval> approvals = transaction.getApprovals();
		
		if (approvals == null) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyApprovedTransaction: inputs is null");
			return false;
		}

		int sumInputsAmount = 0;
		int sumApprovalAmount = 0;
		
		// Case 4:
		for (Input input : inputs) {
			int amount = input.getAmount();
			if (amount <= NO_COINS) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyApprovedTransaction: input amounts is negative or zero");
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
				System.out.println("DEBUG: verifyApprovedTransaction: output amount is negative or zero");
				return false;
			}
			// sum up for case 5
			sumApprovalAmount += amount;
		}
		
		// Case 5:
		// TODO [joeren]: implementation of approval-exception
		if (sumApprovalAmount > sumInputsAmount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyApprovedTransaction: more output than input");
			return false;
		}
		
		//Case 13:
		//TODO [Vitali] Implement the check for the lock script as soon as the Revoke class was introduced. 
		//Till then there is no use in implementing it. 
		//For the time being it only checked that Locking Script is not empty.
		for(Approval approval : approvals){
			String lockingScript = approval.getLockingScript();
			if(lockingScript.isEmpty()){
				System.out.println("DEBUG: verifyApprovedTransaction: locking script is empty.");
				return false;
			}
		}
		
		
		//TODO [Vitali] Implement rest of the verification, if some.
		System.out.println("DEBUG: verifyApprovedTransaction: verified " + transaction.hash());
		return true;
		
		
	}
	
	public boolean verifyCoinbaseTransaction(Transaction transaction, Block toVerifyBlock){
		
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
		
		if(coinBases.size() != 1){
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyCoinbaseTransaction: More then one coinbase transaction.");
			return false;
		}
		
		Output coinBase = coinBases.get(0);
		
		int currentReward = coinBase.getAmount();
		int trueReward = toVerifyBlock.rewardCalculator();
		if(trueReward != currentReward){
			System.out.println(String.format("DEBUG: verifyCoinbaseTransaction: amount %d doesn't equal reward %d", currentReward, trueReward));
//			return false;
		}
		
		// #6
		if (transaction.getInputsCount() > 0)
		{
			return false;
		}
		
		//TODO [Vitali] Implement rest of the verification, if some.
		return true;
		
	}
	
	public boolean verifyRegularTransaction(Transaction transaction) {

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
		String hashedTransaction = transaction.hash().toString();
		for(Input input : transaction.getInputs()){
			
			signature = input.getUnlockingScript(EInputUnlockingScript.SIGNATURE);
				
			if(this.wallet.checkSignature(hashedTransaction, signature)){
				System.out.println("INFO: verifyRegularTransaction: Signature is correct.");
				break; 
			}
					
		}
		
		//TODO [Vitali] Implement rest of the verification, if some.
		System.out.println("DEBUG: verifyRegularTransaction: verified " + transaction.hash());
		return true;
	}

	public boolean verifyRevokeTransaction(Transaction transaction) {
		Transaction transRevoked = this.blockChain.getTransaction(transaction.getApprovedTransaction());
		if (transRevoked == null) {
			System.out.println("DEBUG: verifyRevokeTransaction: Transaction be be revoked cannot be found");
			return false;
		}

		List<Input> inputs = transaction.getInputs();
		List<Approval> approvals = transRevoked.getApprovals();

		if (approvals == null) {
			System.out.println("DEBUG: verifyRevokeTransaction: approvals are null");
			return false;
		}

		if (inputs == null) {
			System.out.println("DEBUG: verifyRevokeTransaction: inputs are null");
			return false;
		}

		if (transRevoked.getOutputsCount() != 0) {
			System.out.println("DEBUG: verifyRevokeTransaction: revoked transaction has outputs");
			return false;
		}

		int sumInputsAmount = 0;
		int sumApprovalAmount = 0;

		for (Input input : inputs) {
			if (input.getAmount() <= 0) {
				System.out.println("DEBUG: verifyRevokeTransaction: input amounts is negative or zero");
				return false;
			}
			sumInputsAmount += input.getAmount();
		}

		for(Approval approval : approvals){
			if (approval.getAmount() <= 0) {
				System.out.println("DEBUG: verifyRevokeTransaction: approved amount is negative or zero");
				return false;
			}
			sumApprovalAmount += approval.getAmount();
		}

		if (sumApprovalAmount != sumInputsAmount) {
			System.out.println("DEBUG: verifyRevokeTransaction: sum of input and approval don't match");
			return false;
		}

		System.out.println("DEBUG: verifyRevokeTransaction: verified " + transaction.hash());
		return true;
	}
	
	private boolean verifyMerkle(Block block) {
		Sha256Hash merkle = block.getHashMerkleRoot();
		BinaryTree<Transaction> tree = new BinaryTree<>(block.getTransactions());
		return tree.getRoot().hash().equals(merkle);
	}
	


}
