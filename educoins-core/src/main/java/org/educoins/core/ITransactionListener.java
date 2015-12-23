package org.educoins.core;

import org.educoins.core.transaction.Transaction;

public interface ITransactionListener {
	
	
	void transactionReceived(Transaction transaction);

}
