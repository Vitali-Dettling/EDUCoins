package org.educoins.core.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.educoins.core.Wallet;
import org.jetbrains.annotations.NotNull;

public class CoinbaseTransaction extends Transaction {

	public int amount;
	public String publicKey;
	
	public CoinbaseTransaction(int amount, String publicKey) {
		this.amount = amount;
		this.publicKey = publicKey;
	}

	@Override
	public Transaction create() {
		
		//Each output coins only a single coin.
		//That later we do not have to deal with odd number of coins and reverse transactions.
		for(int i = 0 ; i < amount ; i++){
			Output out = new Output(1, publicKey);
			super.addOutput(out);
		}
		return this;
	}

}
