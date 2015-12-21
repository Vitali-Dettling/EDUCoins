package org.educoins.core.transaction;

import org.educoins.core.Approval;
import org.educoins.core.Input;
import org.educoins.core.Transaction;

public class ApprovedTransaction {

	public Transaction generateApprovedTransaction(int amount, String owner, String holder, String lockingScript) {

		//All fake data, just for testing. (Delete)
		Transaction buildTx = new Transaction(); 
		Input in = new Input(3, "", "");
		Approval app = new Approval(amount, owner, holder, lockingScript);
		
		buildTx.addInput(in);
		buildTx.addApproval(app);
		return buildTx;
	}

	//TODO Implementation
}
