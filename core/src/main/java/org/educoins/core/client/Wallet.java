package org.educoins.core.client;

import java.io.BufferedReader;
import java.io.Console;

import org.educoins.core.miner.Block;

import Transactions.Transaction;

public final class Wallet {

	private static String walletAddress;

	public static void enterWalletAddress(String EDUCoinAddress) {

		//TODO: Asking for wallet address...
		walletAddress = EDUCoinAddress;

	}
	
	
	public static String getWalletAddress() {
		return walletAddress;
	}

	
	

	

}
