package org.educoins.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.transaction.ApprovedTransaction;
import org.educoins.core.transaction.RegularTransaction;
import org.educoins.core.transaction.RevokedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

	private final Logger logger = LoggerFactory.getLogger(Client.class);

	protected static Wallet wallet;	
	private static List<Output> previousOutputs;
	private static List<Block> blockBuffer;
	private static int availableAmount;
	private static boolean locked;
	
	public Client(Wallet wallet){
		Client.wallet = wallet;
		Client.previousOutputs = new ArrayList<>();
		Client.blockBuffer = new ArrayList<>();
		Client.availableAmount = 0;
		Client.locked = false;
	}
	
	public Transaction generateRevokeTransaction(int amount, String lockingScript) {
		//TODO need real implementation.
		RevokedTransaction approvedTransaction = new RevokedTransaction();
		Transaction buildTx = approvedTransaction.generateRevokedTransaction(amount, lockingScript);
		return buildTx;
	}
	
	public Transaction generateApprovedTransaction(int amount, String owner, String holder, String lockingScript){
		//TODO need real implementation.
		ApprovedTransaction approvedTransaction = new ApprovedTransaction();
		Transaction buildTx = approvedTransaction.generateApprovedTransaction(amount, owner, holder, lockingScript);
		return buildTx;
	}

	public Transaction generateRegularTransaction(int sendAmount, String publicKey) {
		
		if(!checkAmount(sendAmount) || !checkOutputs()){
			return null;
		}
		
		Client.locked = true;
		Client.availableAmount -= sendAmount;
		RegularTransaction regTx = new RegularTransaction(Client.wallet, previousOutputs);	
		Transaction buildTx = regTx.generateRegularTransaction(sendAmount, publicKey);
		Client.locked = false;
		
		return buildTx;
	}
	
	private boolean checkOutputs(){
		if (previousOutputs.isEmpty()) {
			logger.info("You have never got any EDUCoins.");
			return false;
		}
		return true;
	}


	private boolean checkAmount(int sendAmount) {

		int availableAmount = getAmount();
		if ((availableAmount - sendAmount) < 0) {
			logger.info("Not enough amount.");
			return false;
		}
		return true;
	}

	public void distructOwnOutputs(Block block) {
		if(Client.locked){
			Client.blockBuffer.add(block);
		}else{
			checkBlock(block);
		}
	}

	private void checkBlock(Block block) {
		
		List<String> publicKeys = Client.wallet.getPublicKeys();

		for (org.educoins.core.Transaction tx : block.getTransactions()) {
			for (Output out : tx.getOutputs()) {
				for (String publicKey : publicKeys) {
					if (out.getLockingScript().equals(publicKey)) {
						Client.previousOutputs.add(out);
						Client.availableAmount += out.getAmount();
					}
				}
			}
		}
		
		if(!Client.blockBuffer.isEmpty()){
			for(Block bufferedBlock : Client.blockBuffer){
				Client.locked = true;
					checkBlock(bufferedBlock);
					Client.blockBuffer.clear();
				Client.locked = false;
			}
		}
	}
	
	public int getAmount(){
		return Client.availableAmount;
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

