package org.educoins.core.transaction;

import org.educoins.core.utils.Sha256Hash;

public class RevokeTransaction extends Transaction {
	
	private Sha256Hash transToRevokeHash;
	private String lockingScript;
	
	public RevokeTransaction(Sha256Hash transToRevokeHash, String lockingScript) {
		this.transToRevokeHash = transToRevokeHash;
		this.lockingScript = lockingScript;
	}



	@Override
	public Transaction create() {
		// TODO 
		this.setApprovedTransaction(hash());
		for (int i = 0; i < approvals.size(); i++) {
			Input input = new Input(approvals.get(i).getAmount(), hash().toString(), "TODO");
			this.addInput(input);
		}
		this.setOutputs(outputs);
		setOutputs(null);
		
		return this;
	}
	
}
	
