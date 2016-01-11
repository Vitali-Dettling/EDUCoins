package org.educoins.core.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class ApprovedTransaction extends Transaction {

	private int amount;
	private String owner;
	private String holder;
	private String lockingScript;
	private List<Output> previousOutput;
	
	public ApprovedTransaction(@NotNull List<Output> previousOutput, int amount, String owner, String holder, String lockingScript) {
		this.amount = amount;
		this.owner = owner;
		this.holder = holder;
		this.lockingScript = lockingScript;
		this.previousOutput = previousOutput;
	}
	
	@Override
	public Transaction create() {

		List<String> hashPreviousOutput = getHashPreviousOutput();
		for(String hashedOutput : hashPreviousOutput){
			Approval approval = new Approval(hashedOutput, amount, owner, holder, lockingScript);
			super.addApproval(approval);
		}
		return this;
	}

	private List<String> getHashPreviousOutput() {
		
		int enoughApproved = 0;
		List<String> previousHashOutputs = new ArrayList<String>();	
		Iterator<Output> iterator = this.previousOutput.iterator();
		while(iterator.hasNext()){
			
			Output nextOut = iterator.next();
			enoughApproved += nextOut.getAmount();
			previousHashOutputs.add(nextOut.hash().toString());
			iterator.remove();
			
			if(enoughApproved == amount){
				break;
			}
		}
		
		return previousHashOutputs;
	}

	
}
