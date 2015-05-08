package org.educoins.core.client;

import java.io.BufferedReader;
import java.io.Console;

import org.educoins.core.miner.Block;

public final class Wallet {

	private static String walletAddress;
	private static Transaction transaction;

	public static void enterWalletAddress() {

		//TODO: Asking for wallet address...
		walletAddress = "Address";
		
		transaction = new Transaction();

	}

	public static void reward(Block block) {
		
		//Increase the transaction number by one...
		block.setnTx(block.getnTx()+1);
		
		
	
		
		

	}

}
