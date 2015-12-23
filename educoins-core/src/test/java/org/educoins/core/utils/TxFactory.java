package org.educoins.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Wallet;
import org.educoins.core.transaction.Output;

public class TxFactory {

	
	public static List<Output> getRandomPreviousOutputs(){
		
		List<Output> outputs = new ArrayList<Output>();
		String publicKey = Wallet.getPublicKey();
		
		for(int i = 1 ; i < 20 ; i++){
			Output out = new Output(i, publicKey);
			outputs.add(out);
		}
	
		return outputs;
	}
}
