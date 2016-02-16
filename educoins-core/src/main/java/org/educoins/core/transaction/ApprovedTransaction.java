package org.educoins.core.transaction;

import java.util.List;

public class ApprovedTransaction extends Transaction {

	private String holderSignature;

	public ApprovedTransaction(List<Approval> approvals, List<Output> outputs, List<Input> inputs, String holderSignature) {
		this.inputs = inputs;
		this.approvals = approvals;
		this.outputs = outputs;
		this.holderSignature = holderSignature;
	}

	@Override
	public Transaction create() {
		//super.signApprovals(this.holderSignature);
		super.signInputs();
		return this;
	}
}
