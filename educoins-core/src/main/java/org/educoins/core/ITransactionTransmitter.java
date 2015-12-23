package org.educoins.core;

import org.educoins.core.transaction.Transaction;

public interface ITransactionTransmitter {

	void transmitTransaction(Transaction transaction);
	
}
