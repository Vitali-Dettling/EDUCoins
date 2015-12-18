package org.educoins.core.transaction;

import org.educoins.core.Transaction;

public interface ITransaction {

	Transaction generateRegularTransaction(int sendAmount, String publicKey);

	
}
