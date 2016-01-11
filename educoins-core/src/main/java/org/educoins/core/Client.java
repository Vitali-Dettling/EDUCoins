package org.educoins.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.transaction.Approval;
import org.educoins.core.transaction.ITransactionFactory;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.TransactionFactory;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

	private final Logger logger = LoggerFactory.getLogger(Client.class);

	private List<Output> previousOutputs;
	private List<Approval> approvedTransactions;
	private ITransactionFactory transactionFactory;
	private List<Block> blockBuffer;
	private static int availableAmount;
	private static int approvedCoins;
	private boolean locked;
	
	public Client(){
		this.previousOutputs = new ArrayList<>();
		this.approvedTransactions = new ArrayList<>();
		
		Client.availableAmount = 0;
		Client.approvedCoins = 0;
		
		this.transactionFactory = new TransactionFactory();
		this.blockBuffer = new ArrayList<>();
		this.locked = false;
	}
	
	public Transaction generateRevokeTransaction(int amount, String lockingScript) {
		//TODO 
		return null;
	}
	
	public Transaction generateApprovedTransaction(int toApproveAmount, String owner, String lockingScript){

		if(!checkAmount(toApproveAmount) || !checkOutputs() || !checkParams(owner) || !checkParams(lockingScript)){
			return null;
		}
		
		this.locked = true;
		Client.availableAmount -= toApproveAmount;
		Transaction buildTx = this.transactionFactory.generateApprovedTransaction(this.previousOutputs, toApproveAmount, owner, lockingScript);
		this.locked = false;
		
		return buildTx;
	}

	public Transaction generateRegularTransaction(int sendAmount, String publicKey) {
		
		if(!checkAmount(sendAmount) || !checkOutputs() || !checkParams(publicKey)){
			return null;
		}
		
		this.locked = true;
		Client.availableAmount -= sendAmount;
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
			this.logger.info("You have never got any EDUCoins.");
			return false;
		}
		return true;
	}


	private boolean checkAmount(int sendAmount) {

		int availableAmount = getAmount();
		if ((availableAmount - sendAmount) < 0) {
			this.logger.info("Not enough amount. Available: " + Client.availableAmount + " which to send: " + sendAmount);
			return false;
		}
		return true;
	}

	public void distructOwnOutputs(Block block) {
		if(this.locked){
			this.blockBuffer.add(block);
		}else{
			checkBlock(block);
		}
		this.logger.info("You have received some EDUCoins; the current amount is: " + Client.availableAmount);
	}

	private void checkBlock(Block block) {
		
		List<String> publicKeys = Wallet.getPublicKeys();

		for (Transaction tx : block.getTransactions()) {
			if(tx.whichTransaction() ==  ETransaction.REGULAR){
				for (Output out : tx.getOutputs()) {
					for (String publicKey : publicKeys) {
						if (out.getLockingScript().equals(publicKey)) {
							this.previousOutputs.add(out);
							Client.availableAmount += out.getAmount();
						}
					}
				}
			}else if(tx.whichTransaction() == ETransaction.APPROVED){
				for (Approval app : tx.getApprovals()) {
					for (String publicKey : publicKeys) {
						if (app.getLockingScript().equals(publicKey)) {
							this.approvedTransactions.add(app);
							Client.approvedCoins += app.getAmount();	
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
	
	public int getAmount(){
		return Client.availableAmount;
	}
	
	public int getApproveCoins(){
		return Client.approvedCoins;
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
}

