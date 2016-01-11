package org.educoins.core.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.educoins.core.Wallet;
import org.jetbrains.annotations.NotNull;

public class RegularTransaction extends Transaction {

	private  List<Output> previousOutputs;
	private int sendAmount;
	private int inputAmount;
	private String sendPublicKey;
	
	public RegularTransaction(@NotNull List<Output> previousOutputs, int sendAmount, int inputAmount, String sendPublicKey) {
		this.previousOutputs = previousOutputs;
		this.sendAmount = sendAmount;
		this.inputAmount = inputAmount;
		this.sendPublicKey = sendPublicKey;
	}
	
	@Override
	public Transaction create(){
		
		List<Output> outputs = createOutputs();
		super.setOutputs(outputs);
		List<Input> inputs = createInputs();
		super.setInputs(inputs);
		super.signInputs();
		return this;
	}
	
	public List<Input> createInputs() {

		int enoughApproved = 0;
		List<Input> inputs = new ArrayList<>();
		Iterator<Output> iterator = previousOutputs.iterator();
		while(iterator.hasNext()){
			Output out = iterator.next();
			enoughApproved += out.getAmount();
			Input in = new Input(out.getAmount(), out.hash().toString(), out.getLockingScript());
			inputs.add(in);
			
			if(enoughApproved == sendAmount){
				break;
			}
		}
		return inputs;
	}
	
	public List<Output> createOutputs() {
		// Send to output.
		List<Output> outputs = new ArrayList<>();
		Output out1 = new Output(sendAmount, sendPublicKey);
		outputs.add(out1);

		String reversePublicKey = Wallet.getPublicKey();

		// Back to the owner output.
		int reverseAmount = inputAmount - sendAmount;
		if (reverseAmount > 0) {
			// Only a reverse amount if there is one. Otherwise the transaction
			// would not verify.
			Output out2 = new Output(reverseAmount, reversePublicKey);
			outputs.add(out2);
		}
		return outputs;
	}

	




}
