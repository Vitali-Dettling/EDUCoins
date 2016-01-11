package org.educoins.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.transaction.ITransactionFactory;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.TransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

	private final Logger logger = LoggerFactory.getLogger(Client.class);

	private List<Output> previousOutputs;
	private ITransactionFactory transactionFactory;
	private List<Block> blockBuffer;
	private int availableAmount;
	private boolean locked;
	
	public Client(){
		this.previousOutputs = new ArrayList<>();
		this.transactionFactory = new TransactionFactory();
		this.blockBuffer = new ArrayList<>();
		this.availableAmount = 0;
		this.locked = false;
	}
	
	public Transaction generateRevokeTransaction(int amount, String lockingScript) {
		//TODO 
		return null;
	}
	
	public Transaction generateApprovedTransaction(int toApproveAmount, String owner, String holder, String lockingScript){
		
		if(!checkAmount(toApproveAmount) || !checkOutputs() || !checkParams(owner) || !checkParams(holder) || !checkParams(lockingScript)){
			return null;
		}
		
		this.locked = true;
		this.availableAmount -= toApproveAmount;
		Transaction buildTx = this.transactionFactory.generateApprovedTransaction(this.previousOutputs, toApproveAmount, owner, holder, lockingScript);
		this.locked = false;
		
		return buildTx;
	}

	public Transaction generateRegularTransaction(int sendAmount, String publicKey) {
		
		if(!checkAmount(sendAmount) || !checkOutputs() || !checkParams(publicKey)){
			return null;
		}
		
		this.locked = true;
		this.availableAmount -= sendAmount;
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
			this.logger.info("Not enough amount. Available: " + this.availableAmount + " which to send: " + sendAmount);
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
		this.logger.info("You have received some EDUCoins; the current amount is: " + this.availableAmount);
	}

	private void checkBlock(Block block) {
		
		List<String> publicKeys = Wallet.getPublicKeys();

		for (Transaction tx : block.getTransactions()) {
			for (Output out : tx.getOutputs()) {
				for (String publicKey : publicKeys) {
					if (out.getLockingScript().equals(publicKey)) {
						this.previousOutputs.add(out);
						this.availableAmount += out.getAmount();
					}
				}
			}
		}
		
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
		return this.availableAmount;
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

