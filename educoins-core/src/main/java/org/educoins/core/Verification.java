package org.educoins.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.transaction.Approval;
import org.educoins.core.transaction.Input;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Revoke;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.BinaryTree;
import org.educoins.core.utils.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Verification {

	private static final int TRUE = 0;
	private static final int ZERO = 0;
	private static final int NO_COINS = 0;
	private static final int HAS_NO_ENTRIES = 0;
	private static final String GENESIS_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";

	private BlockChain blockChain;
	private Logger logger = LoggerFactory.getLogger(BlockChain.class);

	private HashMap<Sha256Hash, Integer> usedOutputs;

	public Verification(BlockChain blockChain) {
		this.usedOutputs = new HashMap<>();
		this.blockChain = blockChain;
	}

	public boolean verifyBlockChain(BlockChain newChain) {
		assert newChain != null;

		this.blockChain = newChain;
		this.usedOutputs.clear();
		try {
			Iterator<Block> it = blockChain.getBlocks().iterator();
			Block currentBlock;
			while (it.hasNext()) {
				currentBlock = it.next();
				// verify block, if fails, return false
				if (!verifyBlock(currentBlock))
					return false;
			}
		} catch (BlockNotFoundException e) {
			return false;
		}
		return true;
	}

	private boolean verifyAllTransactions(Block currentBlock) {
		for (Transaction transaction : currentBlock.getTransactions()) {
			String transHash = transaction.hash().toString();
			// put all outputs to list and set to "not used"
			for (int i = 0; i < transaction.getOutputsCount(); i++) {
				Sha256Hash hash = transaction.getOutputs().get(i).hash();
				if (usedOutputs.containsKey(hash)){
					usedOutputs.replace(hash, usedOutputs.get(hash));
				} else{
					usedOutputs.put(transaction.getOutputs().get(i).hash(), 1);
				}
			}
			for (Input input : transaction.getInputs()) {
				int uses = usedOutputs.getOrDefault(input.getHashPrevOutput(), 0);
				// if output is already "used" return false
				if (uses <= 0) {
					return false;
				} else {
					// If Key wasn't set, there was no output, otherwise set
					// used to true
					uses -= 1;
					if (usedOutputs.replace(input.getHashPrevOutput(), uses) == null) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean verifyBlock(Block toVerifyBlock) {

		if (toVerifyBlock == null) {
			throw new NullPointerException("Block is null.");
		}

		// 0. If block is the genesis block return true, because there is no
		// previous.
		if (toVerifyBlock.getHashPrevBlock().equals(Sha256Hash.wrap(GENESIS_BLOCK))) {
			logger.debug("Genesis Block successfully verfified, hash: {}", toVerifyBlock.hash());
			return true;
		}

		// 1. Find the previous block.
		Block previousBlock = null;
		try {
			previousBlock = this.blockChain.getPreviousBlock(toVerifyBlock);
		} catch (BlockNotFoundException e) {
			logger.warn("verifyBlock: previousBlock is not correct. The block order is most likely wrong.");
			//return false;
		}

		// 3. Are the hashes equal of the current block and the previous one?
		if (previousBlock == null || toVerifyBlock.hash().compareTo(previousBlock.getHashPrevBlock()) == TRUE) {
			logger.warn("verifyBlock: last block is equal to block");
			return false;
		}

		// 4. At least one transaction has to be in the block, namely the
		// coinbase transaction.
		if (toVerifyBlock.getTransactions().size() <= HAS_NO_ENTRIES) {
			logger.warn("verifyBlock: no transactions");
			return false;
		}

		// 5. Verification of all transactions in a block.
		boolean isTransactionValid = false;
		List<Transaction> transactions = toVerifyBlock.getTransactions();
		for (Transaction transaction : transactions) {
			// 5.1 Check for transaction type.
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
			default:
				logger.warn("verifyBlock: transaction could not be determined. " + toVerifyBlock.toString());
			}

			// As soon as a transaction is not valid, the loop will be
			// cancelled.
			if (!isTransactionValid) {
				return false;
			}
		}

		// 6. verify inputs
		if (!verifyAllTransactions(toVerifyBlock)) {
			logger.warn("verifyBlock: transaction inputs are not valid!");

			//return false;
		}

		if (!verifyMerkle(toVerifyBlock)) {
			logger.warn("verifyBlock: verfication of merkle root failed");
			return false;
		}
		return true;
	}

	public boolean verifyApprovedTransaction(Transaction transaction) {
		List<Input> inputs = transaction.getInputs();
		List<Approval> approvals = transaction.getApprovals();

		if (approvals == null) {
			logger.warn("verifyApprovedTransaction: inputs is null");
			return false;
		}

		// Case 4:
		for (Input input : inputs) {
			int amount = input.getAmount();
			if (amount <= NO_COINS) {
				logger.warn("verifyApprovedTransaction: input amounts is negative or zero");
				return false;
			}
		}

		for (Approval approval : approvals) {
			if (approval.getAmount() <= NO_COINS) {
				logger.warn("verifyApprovedTransaction: approved amound is 0");
				return false;
			}

			int amount = approval.getAmount();
			if (amount <= NO_COINS) {
				logger.warn("verifyApprovedTransaction: output amount is negative or zero");
				return false;
			}

		}

		// Case 5:
		int AmountOfApprovalsInTransaction = approvals.stream().mapToInt(a -> a.getAmount()).sum();
		int AmountOfInputsInTransaction = inputs.stream().mapToInt( i -> i.getAmount()).sum();
		if (AmountOfApprovalsInTransaction > AmountOfInputsInTransaction) {
			logger.warn("verifyApprovedTransaction: more output than input");
			return false;
		}

		// Case 13:
		// TODO Implement the check for the lock script as soon as the
		// Revoke class was introduced.
		// Till then there is no use in implementing it.
		// For the time being it only checked that Locking Script is not empty.
		for (Approval approval : approvals) {
			String lockingScript = approval.getLockingScript();
			if (lockingScript.isEmpty()) {
				logger.warn("DEBUG: verifyApprovedTransaction: locking script is empty.");
				return false;
			}
		}

		logger.info("verifyApprovedTransaction: verified " + transaction.hash());
		return true;

	}

	public boolean verifyCoinbaseTransaction(Transaction transaction, Block toVerifyBlock) {

		// After "Bildungsnachweise als Digitale Währung - eine Anwendung der
		// Block-Chain-Technologie" p. 37f

		// Case 1:
		List<Output> coinBases = transaction.getOutputs();

		if (coinBases == null) {
			logger.warn("verifyCoinbaseTransaction: output is null");
			return false;
		}

		if (coinBases.size() != 1) {
			logger.warn("verifyCoinbaseTransaction: More then one coinbase transaction.");
			return false;
		}

		Output coinBase = coinBases.get(0);

		int currentReward = coinBase.getAmount();
		int trueReward = toVerifyBlock.rewardCalculator();
		if (trueReward != currentReward) {
			logger.warn(String.format("verifyCoinbaseTransaction: amount %d doesn't equal reward %d", currentReward,
					trueReward));
		}

		// #6
		return transaction.getInputsCount() <= 0;
	}

	public boolean verifyRegularTransaction(Transaction transaction) {
		// After "Bildungsnachweise als Digitale Währung - eine Anwendung der
		// Block-Chain-Technologie" p. 37f
		// Case 1:
		List<Input> inputs = transaction.getInputs();

		if (inputs == null) {
			logger.warn("verifyRegularTransaction: inputs is null");
			return false;
		}

		int realInputsCount = inputs.size();

		if (realInputsCount == ZERO) {
			logger.warn("verifyRegularTransaction: realInputsCount is 0");
			return false;
		}

		int inputsCount = transaction.getInputsCount();

		if (realInputsCount != inputsCount) {
			logger.warn("verifyRegularTransaction: realInputsCount does not match inputsCount");
			return false;
		}

		List<Output> outputs = transaction.getOutputs();

		if (outputs == null) {
			logger.warn("verifyRegularTransaction: outputs is null");
			return false;
		}

		int realOutputsCount = outputs.size();

		if (realOutputsCount == ZERO) {
			logger.warn("verifyRegularTransaction: realOutputsCount is 0");
			return false;
		}

		int outputsCount = transaction.getOutputsCount();

		if (realOutputsCount != outputsCount) {
			logger.warn("verifyRegularTransaction: realOutputsCount does not match outputsCount");
			return false;
		}

		// Case 4:
		if (inputs.stream().mapToInt(i -> i.getAmount()).sum() <= 0 ){
			logger.warn("verifyRegularTransaction: input amounts is negative or zero");
			return false;
		}

		if (outputs.stream().mapToInt(o -> o.getAmount()).sum() <= 0 ){
			logger.warn("verifyRegularTransaction: output amounts is negative or zero");
			return false;
		}

		// Case 13:
		// TODO The check is current done with the ECDSA class but
		// actually that should be done through the script language.
		// Currently it check just whether the signature corresponds with one
		// public key in the wallet file.
		String signature = null;
		String hashedTransaction = transaction.hash().toString();
		for (Input input : transaction.getInputs()) {

			signature = input.getSignature();

			if (Wallet.checkSignature(hashedTransaction, signature)) {
				logger.info("INFO: verifyRegularTransaction: Signature is correct.");
				break;
			}

		}

		logger.info("verifyRegularTransaction: verified " + transaction.hash());
		return true;
	}

	public boolean verifyRevokeTransaction(Transaction transaction) {
		Transaction transRevoked = this.blockChain.getTransaction(transaction.getApprovedTransaction());
		if (transRevoked == null) {
			logger.warn("verifyRevokeTransaction: Transaction be be revoked cannot be found");
			return false;
		}

		List<Input> inputs = transaction.getInputs();
		List<Approval> approvals = transRevoked.getApprovals();

		if (approvals == null) {
			logger.warn("verifyRevokeTransaction: approvals are null");
			return false;
		}

		if (inputs == null) {
			logger.warn("verifyRevokeTransaction: inputs are null");
			return false;
		}

		if (transRevoked.getOutputsCount() != 0) {
			logger.warn("verifyRevokeTransaction: revoked transaction has outputs");
			return false;
		}

		int sumInputsAmount = 0;
		int sumApprovalAmount = 0;

		for (Input input : inputs) {
			if (input.getAmount() <= 0) {
				logger.warn("verifyRevokeTransaction: input amounts is negative or zero");
				return false;
			}
			sumInputsAmount += input.getAmount();
		}

		for (Approval approval : approvals) {
			if (approval.getAmount() <= 0) {
				logger.warn("verifyRevokeTransaction: approved amount is negative or zero");
				return false;
			}
			sumApprovalAmount += approval.getAmount();
		}

		logger.info("verifyRevokeTransaction: verified " + transaction.hash());
		return true;
	}

	private boolean verifyMerkle(Block block) {
		Sha256Hash merkle = block.getHashMerkleRoot();
		BinaryTree<Transaction> tree = new BinaryTree<>(block.getTransactions());
		return tree.getRoot().hash().equals(merkle);
	}

	public boolean approvalValide(String stillApproved) {

		try {
			for (Block block : this.blockChain.getBlocks()) {
				for (Transaction tx : block.getTransactions()) {
					for (Revoke rev : tx.getRevokes()) {
						if (rev.getHashPrevApproval().toString().equals(stillApproved)) {
							return false;
						}
					}
				}
			}
		} catch (BlockNotFoundException e) {
			logger.error("While checking the approved transaction against the revoke transaction. ");
		}

		return true;
	}

}
