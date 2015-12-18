package org.educoins.core.transaction;

import java.util.ArrayList;
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
	public Transaction generateRegularTransaction(int sendAmount, String publicKey){
		
		Transaction tx = new Transaction();

		List<Input> inputs = new ArrayList<>();
		for (Output out : previousOutputs) {
			Input in = new Input(out.getAmount(), hashPrevOutput(out), out.getLockingScript());
			inputs.add(in);	
		}
		tx.setInputs(inputs);
		
		List<Output> outputs = new ArrayList<>();
		Output out1 = new Output(sendAmount, publicKey, publicKey);
		outputs.add(out1);
		String reversPublicKey = this.wallet.getPublicKey();
		Output out2 = new Output(sendAmount, reversPublicKey, reversPublicKey);
		outputs.add(out2);
		
		tx.setOutputs(outputs);
		return tx;
	}
	
	private String hashPrevOutput(Output output){
		byte[] toBeHashed = output.getConcatedOutput();
		return Sha256Hash.wrap(SHA256Hasher.hash(toBeHashed)).toString();
	}



}
