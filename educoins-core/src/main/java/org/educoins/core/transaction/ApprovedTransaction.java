package org.educoins.core.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.educoins.core.Wallet;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;

public class ApprovedTransaction extends Transaction {

	private int amount;
	private String owner;
	private String lockingScript;
	private String holderSignature;
	private List<Output> previousOutput;
	
	public ApprovedTransaction(@NotNull List<Output> previousOutput, int amount, String owner, String holderSignature, String lockingScript) {
		this.amount = amount;
		this.owner = owner;
		this.lockingScript = lockingScript;
		this.holderSignature = holderSignature;
		this.previousOutput = previousOutput;
	}
	
	@Override
	public Transaction create() {

		List<String> hashPreviousOutput = getHashPreviousOutput();
		for(int i = 0 ; i < amount ; i++){
			Approval approval = new Approval(hashPreviousOutput.get(i), amount, owner, lockingScript);
			super.addApproval(approval);
			approval.setHolderSignature(this.holderSignature);
		}
		return this;
	}

	private List<String> getHashPreviousOutput() {
		
		List<String> previousHashOutputs = new ArrayList<String>();	
		Iterator<Output> iterator = this.previousOutput.iterator();
		for(int i = 0 ; i < amount ; i++){
			
			if(iterator.hasNext()){
				Output nextOut = iterator.next();
				int currentAmount = nextOut.getAmount();
				previousHashOutputs.add(nextOut.hash().toString());
				nextOut.setAmount(currentAmount - amount);
				
				if(nextOut.getAmount() == 0){
					iterator.remove();
				}
				
			}
		}
		
		return previousHashOutputs;
	}

	
}
