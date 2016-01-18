package org.educoins.core.transaction;

import java.util.List;

public class RegularTransaction extends Transaction {
	
//	List<Input> inputs;
//	List<Output> outputs;
	
	public RegularTransaction(List<Output> outputs, List<Input> inputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	@Override
	public Transaction create(){
//		super.setOutputs(this.outputs);
//		super.setInputs(this.inputs);
		super.signInputs();
		return this;
	}
	


	




}
