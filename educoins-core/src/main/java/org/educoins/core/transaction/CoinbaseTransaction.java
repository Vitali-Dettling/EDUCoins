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
		
		//TODO redo
		Output out = new Output(amount, publicKey);
		super.setOutputs(Arrays.asList(out));

		return this;
	}

}
