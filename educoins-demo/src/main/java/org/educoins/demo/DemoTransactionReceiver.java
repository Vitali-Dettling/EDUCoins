package org.educoins.demo;

import org.educoins.core.ITransactionListener;
import org.educoins.core.ITransactionReceiver;
import org.educoins.core.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DemoTransactionReceiver implements ITransactionListener, ITransactionReceiver {

	private List<ITransactionListener> transactionListeners;
	private boolean active;

	public DemoTransactionReceiver() {
		this.transactionListeners = new ArrayList<>();
		this.active = false;
	}

	@Override
	public void addTransactionListener(ITransactionListener transactionListener) {
		this.transactionListeners.add(transactionListener);
	}

	@Override
	public void removeTransactionListener(ITransactionListener transactionListener) {
		this.transactionListeners.remove(transactionListener);
	}

	public void notifyTransactionReceived(Transaction transaction) {
		if (this.active) {
			for (int i = 0; i < this.transactionListeners.size(); i++) {
				ITransactionListener listener = this.transactionListeners.get(i);
				listener.transactionReceived(transaction);
			}
		}
	}

	@Override
	public void receiveTransactions() {
		this.active = true;
	}

	@Override
	public void transactionReceived(Transaction transaction) {
		this.notifyTransactionReceived(transaction);
	}

}
