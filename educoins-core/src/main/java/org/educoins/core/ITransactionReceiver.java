package org.educoins.core;

public interface ITransactionReceiver {

	void addTransactionListener(ITransactionListener transactionListener);
	
	void removeTransactionListener(ITransactionListener transactionListener);
	
	void receiveTransactions();
	
}
