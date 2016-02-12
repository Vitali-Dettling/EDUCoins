package org.educoins.core.transaction;

import java.util.List;

import org.educoins.core.utils.Sha256Hash;

public class RevokeTransaction extends Transaction {
	
	private String transToRevokeHash;
	private List<Transaction> approvedTransactions;
	
	public RevokeTransaction(List<Transaction> approvedTransactions, String transToRevokeHash) {
		this.approvedTransactions = approvedTransactions;
		this.transToRevokeHash = transToRevokeHash;
	}
	
	@Override
	public Transaction create() {
		Sha256Hash sha256Tx = Sha256Hash.wrap(this.transToRevokeHash);
		this.setApprovedTransaction(sha256Tx);
		List<Approval> apps = null;
		Sha256Hash hashTx = null;
		for(Transaction tx : this.approvedTransactions){
			hashTx =  tx.hash();
			
			if(hashTx.toString().equals(this.transToRevokeHash.toString())){
				apps = tx.getApprovals();
				break;
			}
		}
		
		Revoke revoke = null;
		if(apps != null){
			for(Approval app : apps){
				//TODO Here check for public key of the approved tx.
				revoke = new Revoke(sha256Tx, app.getAmount(), app.getOwnerAddress());
			}
		}
		
		this.setRevokes(revoke);
		this.signRevokes();
		return this;
	}

//	@Override
//	public Transaction create() {
//		// TODO 
//		
//		this.setApprovedTransaction(hash());
//		for (int i = 0; i < approvals.size(); i++) {
//			Input input = new Input(approvals.get(i).getAmount(), hash(), "TODO");
//			this.addInput(input);
//		}
//		this.setOutputs(outputs);
//		setOutputs(null);
//		
//		return this;
//	}
	
}
	
