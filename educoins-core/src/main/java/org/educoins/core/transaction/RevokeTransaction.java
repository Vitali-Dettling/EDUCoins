package org.educoins.core.transaction;

import java.util.List;

import org.educoins.core.utils.Sha256Hash;

public class RevokeTransaction extends Transaction {
	
	private Sha256Hash transToRevokeHash;
	private List<Transaction> approvedTransactions;
	
	public RevokeTransaction(List<Transaction> approvedTransactions, Sha256Hash transToRevokeHash) {
		this.approvedTransactions = approvedTransactions;
		this.transToRevokeHash = transToRevokeHash;
	}
	
	@Override
	public Transaction create() {
		
		this.setApprovedTransaction(this.transToRevokeHash);
		List<Approval> apps = null;
		for(Transaction tx : this.approvedTransactions){
			Sha256Hash hashTx =  tx.hash();
			
			if(hashTx.toString().equals(this.transToRevokeHash.toString())){
				apps = tx.getApprovals();
				break;
			}
		}
		
		Revoke revoke = null;
		for(Approval app : apps){
			//TODO Here check for public key of the approved tx.
			revoke = new Revoke(app.hash(), app.getAmount(), app.getOwnerAddress());
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
	
