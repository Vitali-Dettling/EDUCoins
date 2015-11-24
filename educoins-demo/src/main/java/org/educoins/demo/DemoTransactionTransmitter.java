package org.educoins.demo;

import org.educoins.core.ITransactionListener;
import org.educoins.core.ITransactionTransmitter;
import org.educoins.core.Transaction;

public class DemoTransactionTransmitter implements ITransactionTransmitter {
	
	private ITransactionListener transactionListener;
	
	public DemoTransactionTransmitter(ITransactionListener transactionListener) {
		this.transactionListener = transactionListener;
	}

	@Override
	public void transmitTransaction(Transaction transaction) {
		this.transactionListener.transactionReceived(transaction);
	}
}
