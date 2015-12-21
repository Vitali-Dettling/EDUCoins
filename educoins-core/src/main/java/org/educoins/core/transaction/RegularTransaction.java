package org.educoins.core.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.educoins.core.Input;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.Wallet;
import org.educoins.core.cryptography.SHA256Hasher;
import org.educoins.core.utils.Sha256Hash;

public class RegularTransaction implements ITransaction {

	private Wallet wallet;
	private List<Output> previousOutputs;
	
	
	public RegularTransaction(Wallet wallet, List<Output> previousOutputs){
		this.wallet = wallet;
		this.previousOutputs = previousOutputs;
	}
	
	@Override
	public Transaction generateRegularTransaction(int sendAmount, String sendPublicKey){
		
		List<Output> copyPreviousOutputs = getEnoughAmount(sendAmount);
		
		//Prepare inputs
		Transaction tx = new Transaction();
		int outputAmount = 0;
		List<Input> inputs = new ArrayList<>();
		for (Output out : copyPreviousOutputs) {
			Input in = new Input(out.getAmount(), hashPrevOutput(out), out.getLockingScript());
			outputAmount += out.getAmount();
			inputs.add(in);	
		}
		tx.setInputs(inputs);
		
		//Send to output.
		List<Output> outputs = new ArrayList<>();
		Output out1 = new Output(sendAmount, sendPublicKey);
		outputs.add(out1);
		
		//Back to the owner putput.
		String reversePublicKey = this.wallet.getPublicKey();
		int reverseAmount = outputAmount - sendAmount;
		if(reverseAmount > 0){
			//Only a reverse amount if there is  one. Otherwise the transaction would not verify.  
			Output out2 = new Output(reverseAmount, reversePublicKey);
			outputs.add(out2);
		}
		tx.setOutputs(outputs);
		
		//Sign the transaction.
		for(Input in : tx.getInputs()){
			String signature = this.wallet.getSignature(in.getUnlockingScript(), tx.hash().toString());
			in.setSignature(signature);
		}
		
		return tx;
	}
	
	
	private List<Output> getEnoughAmount(int sendAmount){
		List<Output> copyPreviousOutputs = new ArrayList<>();
		
		Iterator<Output> iterator = this.previousOutputs.iterator();
		
		int enough = 0;
		while(iterator.hasNext()){
			Output out = iterator.next();
			enough += out.getAmount();
			copyPreviousOutputs.add(out);
			iterator.remove();
			if(enough >= sendAmount){
				break;
			}
		}
		return copyPreviousOutputs;
	}
	
	private String hashPrevOutput(Output output){
		byte[] toBeHashed = output.getConcatedOutput();
		return Sha256Hash.wrap(SHA256Hasher.hash(toBeHashed)).toString();
	}



}
