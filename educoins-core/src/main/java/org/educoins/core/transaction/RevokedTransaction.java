package org.educoins.core.transaction;

import org.educoins.core.Approval;
import org.educoins.core.Input;
import org.educoins.core.Transaction;
import org.educoins.core.utils.Sha256Hash;

public class RevokedTransaction {

	public Transaction generateRevokedTransaction(int amount, String lockingScript) {

		//All fake data, just for testing. (Delete)
		Transaction buildTx = new Transaction(); 
		Input in = new Input(1, "", "");

		buildTx.addInput(in);
		buildTx.setApprovedTransaction(Sha256Hash.MAX_HASH);
		return buildTx;
	}
	
	//TODO Implementation
}
