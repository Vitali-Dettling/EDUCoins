package org.educoins.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.Client;
import org.educoins.core.Wallet;
import org.educoins.core.transaction.Input;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Transaction;

public class TxFactory {

	public static List<Input> getRandomPreviousInputs(){
		List<Input> inputs = new ArrayList<Input>();
		String publicKey = Wallet.getPublicKey();
		
		List<Output> outputs = getRandomPreviousOutputs();

		for(int i = 1 ; i < 19 ; i++){
			Input out = new Input(i, outputs.get(i).hash(), publicKey);
			inputs.add(out);
		}
	
		return inputs;
	}
	
	public static List<Output> getRandomPreviousOutputs(){
		
		List<Output> outputs = new ArrayList<>();
		String publicKey = Wallet.getPublicKey();
		
		for(int i = 1 ; i < 20 ; i++){
			Output out = new Output(i, publicKey);
			outputs.add(out);
		}
	
		return outputs;
	}
	
	public static Block createOutputsStoredInBlock(){
		
		List<Output> outputs = TxFactory.getRandomPreviousOutputs();
		Block block = BlockStoreFactory.getRandomBlock();
		Transaction tx = BlockStoreFactory.generateTransaction(1);
		tx.setOutputs(outputs);
		block.addTransaction(tx);

		return block;
	}
	
	
}
