package org.educoins.core.transaction;

import java.util.List;

public class RegularTransaction extends Transaction {
	
	public RegularTransaction(List<Output> outputs, List<Input> inputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	@Override
	public Transaction create(){
		signInputs();
		return this;
	}
	


	




}
