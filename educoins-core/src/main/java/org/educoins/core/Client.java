package org.educoins.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.transaction.Approval;
import org.educoins.core.transaction.ITransactionFactory;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Revoke;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.educoins.core.transaction.TransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

	private final Logger logger = LoggerFactory.getLogger(Client.class);

	private List<Output> previousOutputs;
	private List<Transaction> approvedTransactions;
	private List<Transaction> revokeTransactions;
	private ITransactionFactory transactionFactory;
	private List<Block> blockBuffer;
	boolean locked;

	public Client() {
		this.previousOutputs = new ArrayList<>();
		this.approvedTransactions = new ArrayList<>();
		this.revokeTransactions = new ArrayList<>();

		this.transactionFactory = new TransactionFactory();
		this.blockBuffer = new ArrayList<>();
		this.locked = false;
	}

	public Client(List<Output> previousOutputs, List<Transaction> approvedTransactions,
			ITransactionFactory transactionFactory, List<Block> blockBuffer) {
		this();
		this.previousOutputs = previousOutputs;
		this.approvedTransactions = approvedTransactions;
		this.transactionFactory = transactionFactory;
		this.blockBuffer = blockBuffer;
	}

	public Transaction generateRevokeTransaction(String transToRevokeHash) {

		if (this.approvedTransactions.isEmpty()) {
			this.logger.warn("There are no approved educoins.");
			return null;
		}
		if (!isApprovedTransactionHash(transToRevokeHash)) {
			this.logger.warn("The transaction is either not an approved one or you haven't approved it.");
			return null;
		}
		
		this.locked = true;
		Transaction buildTx = this.transactionFactory.generateRevokeTransaction(this.approvedTransactions,
				transToRevokeHash);
		buildTx.getRevokes().iterator().next().getAmount();
		this.locked = false;

		return buildTx;
	}

	public Transaction generateApprovedTransaction(int toApproveAmount, String owner, String holderSignature,
			String lockingScript) {

		if (!checkAmount(toApproveAmount) || !checkOutputs() || !checkParams(owner) || !checkParams(holderSignature)
				|| !checkParams(lockingScript)) {
			return null;
		}

		this.locked = true;
		Transaction buildTx = this.transactionFactory.generateApprovedTransaction(this.previousOutputs, toApproveAmount,
				owner, holderSignature, lockingScript);
		this.locked = false;
		return buildTx;
	}

	public Transaction generateRegularTransaction(int sendAmount, String publicKey) {

		if (!checkAmount(sendAmount) || !checkOutputs() || !checkParams(publicKey)) {
			return null;
		}

		this.locked = true;
		Transaction buildTx = this.transactionFactory.generateRegularTransaction(this.previousOutputs, sendAmount,
				publicKey);
		this.locked = false;

		return buildTx;
	}

	private boolean isApprovedTransactionHash(String transToRevokeHash) {

		for (Transaction tx : this.approvedTransactions) {

			if (tx.hash().toString().equals(transToRevokeHash)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkParams(String param) {
		if (param.isEmpty() || param == null) {
			this.logger.warn("Parameteres cannot be null or empty.");
			return false;
		}
		return true;
	}

	private boolean checkOutputs() {
		if (previousOutputs.isEmpty()) {
			this.logger.warn("You have never got any EDUCoins.");
			return false;
		}
		return true;
	}

	private boolean checkAmount(int sendAmount) {

		int availableAmount = getRegularAmount();
		if ((availableAmount - sendAmount) < 0) {
			this.logger.info("Not enough amount. Available: " + availableAmount + " which to send: " + sendAmount);
			return false;
		}
		return true;
	}

	public void ownTransactions(Block block) {
		if (this.locked) {
			this.blockBuffer.add(block);
		} else {
			ownTransactionsSeperator(block);
		}
	}

	/**
	 * Recursive as soon as multiple blocks are found while creating a
	 * transaction.
	 * 
	 */
	private void ownTransactionsSeperator(Block block) {

		for (Transaction tx : block.getTransactions()) {

			switch (tx.transactionType()) {
			case COINBASE:
				checkCoinbase(tx);
				break;
			case REGULAR:
				checkRegular(tx);
				break;
			case APPROVED:
				checkApproved(tx);
				break;
			case REVOKE:
				checkRevoke(tx);
				break;
			default:
				this.logger.info("Type of transaction is unknown.");
			}

			if (!this.blockBuffer.isEmpty()) {
				for (Block bufferedBlock : this.blockBuffer) {
					this.locked = true;
					ownTransactionsSeperator(bufferedBlock);
					this.blockBuffer.clear();
					this.locked = false;
				}
			}
		}
	}

	private void checkRevoke(Transaction tx) {
		for (Revoke rev : tx.getRevokes()) {
			for(Transaction appTx : this.approvedTransactions){
				//Does the revoke belong to one of my approvals?
				if (rev.getHashPrevApproval().toString().equals(appTx.hash().toString())) {
					//Should not count twice the transaction.
					Iterator<Transaction> iter = this.revokeTransactions.iterator();
					if(iter.hasNext()){
						if(!tx.hash().toString().equals(iter.next().hash().toString())){
							this.revokeTransactions.add(iter.next());
						}
					}else{//First Iteration.
						this.revokeTransactions.isEmpty();
						this.revokeTransactions.add(tx);
					}
				}
			}
		}
	}

	private void checkApproved(Transaction tx) {
		for (Approval app : tx.getApprovals()) {
			for (String publicKey : Wallet.getPublicKeys()) {
				//Approved EDUCoins
				if (app.getLockingScript().equals(publicKey)) {
					//Should not count twice the transaction.
					Iterator<Transaction> iter = this.approvedTransactions.iterator();
					if(iter.hasNext()){
						if(!tx.hash().toString().equals(iter.next().hash().toString())){
							this.approvedTransactions.add(tx);
						}
					}else{
						//First Iteration.
						this.approvedTransactions.isEmpty();
						this.approvedTransactions.add(tx);
					}
				}//Reverse Transactions.
				if (app.getOwnerAddress().equals(publicKey)) {
					for (Output out : tx.getOutputs()) {
						this.previousOutputs = new ArrayList<>();
						this.previousOutputs.add(out);
						out.getAmount();
					}
				}
			}
		}
	}

	private void checkRegular(Transaction tx) {

		List<Output> outs = tx.getOutputs();
		
		//Does the transaction belong to me?
		for (String publicKey : Wallet.getPublicKeys()) {
			// Received coins form strangers.
			for(Output out : outs){
				if (out.getLockingScript().equals(publicKey)) {
					this.previousOutputs.add(out);
					// Reverse transaction.
					if(out.getAmount() < getRegularAmount()){		
						this.previousOutputs = new ArrayList<>();
						this.previousOutputs.add(out);
					}
				}
			}
		}
	}

	private void checkCoinbase(Transaction tx) {
		for (Output out : tx.getOutputs()) {
			for (String publicKey : Wallet.getPublicKeys()) {
				if (out.getLockingScript().equals(publicKey)) {
					this.previousOutputs.add(out);
					out.getAmount();
				}
			}
		}
	}
	
	public int getRevokedAmount(){
		int revokeAmount = 0;
		for(Transaction tx : this.revokeTransactions){
			for (Revoke rev : tx.getRevokes()) {
				revokeAmount += rev.getAmount();
			}
		}
		return revokeAmount;
	}

	public int getRegularAmount() {
		int availableAmount = 0;
		for (Output out : this.previousOutputs) {
			availableAmount += out.getAmount();
		}
		return availableAmount;
	}

	public int getApprovedAmount() {
		int approvedCoins = 0;
		for (Transaction tx : this.approvedTransactions) {
			for (Approval app : tx.getApprovals()) {
				for (String publicKey : Wallet.getPublicKeys()) {
					if (app.getLockingScript().equals(publicKey)) {
						approvedCoins += app.getAmount();
					}
				}
			}
		}
		return approvedCoins;
	}

	public int getIntInput(Scanner scanner, String prompt) {
		System.out.print(prompt);
		try {
			return Integer.valueOf(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Please enter a number value!");
			return -1;
		}
	}

	public String getHexInput(Scanner scanner, String prompt) {
		System.out.print(prompt);
		String input = scanner.nextLine();
		try {
			new BigInteger(input, 16);
		} catch (NullPointerException | NumberFormatException e) {
			System.out.println("Please enter a valid hex value!");
			return null;
		}
		return input;
	}

	public List<TransactionVM> getListOfTransactions(BlockChain bc) {
		List<TransactionVM> returnList = new ArrayList<>();

		try {
			for (Block b : bc.getBlocks()) {
				for (Transaction t : b.getTransactions()) {
					if (t.transactionType() != ETransaction.COINBASE) {
						TransactionVM tvm = new TransactionVM();
						tvm.setTransactionType(t.transactionType());
						tvm.setHash(t.hash());
						returnList.add(tvm);
					}
				}
			}
		} catch (BlockNotFoundException e) {
			e.printStackTrace();
		}
		return returnList;
	}
}
