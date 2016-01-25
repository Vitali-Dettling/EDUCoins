package org.educoins.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.transaction.Approval;
import org.educoins.core.transaction.ITransactionFactory;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.TransactionFactory;
import org.educoins.core.utils.Sha256Hash;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

	private final Logger logger = LoggerFactory.getLogger(Client.class);

	private List<Output> previousOutputs;
	private List<Transaction> approvedTransactions;
	private ITransactionFactory transactionFactory;
	private List<Block> blockBuffer;
	private static int availableAmount;
	private static int approvedCoins;
	private boolean locked;
	
	public Client(){
		this.previousOutputs = new ArrayList<>();
		this.approvedTransactions = new ArrayList<>();
		
		availableAmount = 0;
		approvedCoins = 0;
		
		this.transactionFactory = new TransactionFactory();
		this.blockBuffer = new ArrayList<>();
		this.locked = false;
	}
	
	public Transaction generateRevokeTransaction(Sha256Hash transToRevokeHash, String lockingScript) {
		//TODO check if else, lock, available amount
		
		this.locked = true;
		Transaction buildTx = this.transactionFactory.generateRevokeTransaction(this.approvedTransactions, transToRevokeHash, lockingScript);
		this.locked = false;
		
		return buildTx;
	}
	
	public Transaction generateApprovedTransaction(int toApproveAmount, String owner, String holderSignature, String lockingScript){

		if(!checkAmount(toApproveAmount) || !checkOutputs() || !checkParams(owner) || !checkParams(holderSignature) || !checkParams(lockingScript)){
			return null;
		}
		
		this.locked = true;
		availableAmount -= toApproveAmount;
		Transaction buildTx = this.transactionFactory.generateApprovedTransaction(this.previousOutputs, toApproveAmount, owner, holderSignature, lockingScript);
		this.locked = false;
		
		return buildTx;
	}

	public Transaction generateRegularTransaction(int sendAmount, String publicKey) {
		
		if(!checkAmount(sendAmount) || !checkOutputs() || !checkParams(publicKey)){
			return null;
		}
		
		this.locked = true;
		availableAmount -= sendAmount;
		Transaction buildTx = this.transactionFactory.generateRegularTransaction(this.previousOutputs, sendAmount, publicKey);
		this.locked = false;

		return buildTx;
	}
	
	private boolean checkParams(String param){
		if(param.isEmpty() || param == null){
			this.logger.warn("Parameteres cannot be null or empty.");
			return false;
		}
		return true;
	}
	
	private boolean checkOutputs(){
		if (previousOutputs.isEmpty()) {
			this.logger.warn("You have never got any EDUCoins.");
			return false;
		}
		return true;
	}


	private boolean checkAmount(int sendAmount) {

		int availableAmount = getEDICoinsAmount();
		if ((availableAmount - sendAmount) < 0) {
			this.logger.info("Not enough amount. Available: " + availableAmount + " which to send: " + sendAmount);
			return false;
		}
		return true;
	}

	//TODO: WTF does this method name mean?
	public void distructOwnOutputs(Block block) {
		if(this.locked){
			this.blockBuffer.add(block);
		}else{
			checkBlock(block);
		}
	}

	private void checkBlock(Block block) {
		
		List<String> publicKeys = Wallet.getPublicKeys();

		for (Transaction tx : block.getTransactions()) {
			if(tx.whichTransaction() ==  ETransaction.REGULAR ||
			   tx.whichTransaction() ==  ETransaction.COINBASE){
				for (Output out : tx.getOutputs()) {
					for (String publicKey : publicKeys) {
						if (out.getLockingScript().equals(publicKey)) {
							this.previousOutputs.add(out);
							availableAmount += out.getAmount();
							this.logger.info("You have received some EDUCoins; the current amount is: " + availableAmount);
						}
					}
				}
			}
			if(tx.whichTransaction() == ETransaction.APPROVED){
				for (Approval app : tx.getApprovals()) {
					for (String publicKey : publicKeys) {
						if (app.getLockingScript().equals(publicKey)) {
							String holderSignature = app.getHolderSignature();
							String hashTest = "123456789ABCDEF";
							if(Wallet.compare(hashTest, holderSignature, publicKey)){
								this.approvedTransactions.add(tx);
								approvedCoins += app.getAmount();	
							}
						}
					}
				}
			}
		}
		
		//Recursive as soon as multiple blocks are found while creating a transaction.
		if(!this.blockBuffer.isEmpty()){
			for(Block bufferedBlock : this.blockBuffer){
				this.locked = true;
					checkBlock(bufferedBlock);
					this.blockBuffer.clear();
				this.locked = false;
			}
		}
	}
	
	public int getEDICoinsAmount(){
		int amount = 0;
		for(Output out : this.previousOutputs){
			amount += out.getAmount();
		}
		availableAmount = amount;
		return amount;
	}
	
	public int getApprovedCoins(){
		approvedCoins = 0;
		for(Transaction txs : this.approvedTransactions){
			for(Approval app : txs.getApprovals()){
				approvedCoins += app.getAmount(); 
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

	public List<TransactionVM> 	getListOfTransactions(BlockChain bc) {
		List<TransactionVM> returnList = new ArrayList<>();
		try {
			for (Block b : bc.getBlocks()) {
				for (Transaction t : b.getTransactions()) {
					if (t.whichTransaction() != ETransaction.COINBASE) {
						TransactionVM tvm = new TransactionVM();
						tvm.setTransactionType(t.whichTransaction());
						tvm.setHash(t.hash());
						tvm.setAmount(t.getAmount(Wallet.getPublicKey()));
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

