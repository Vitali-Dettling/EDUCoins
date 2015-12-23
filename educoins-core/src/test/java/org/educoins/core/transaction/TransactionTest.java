package org.educoins.core.transaction;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Wallet;
import org.educoins.core.transaction.Approval;
import org.educoins.core.transaction.Input;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.educoins.core.transaction.TransactionFactory;
import org.educoins.core.utils.TxFactory;

public class TransactionTest {

	// Coinbase:
	// inputs = 0;
	// outputs > 0;
	// approvals = 0;
	@Test
	public void testWhichTransactionCoinbase() {

		final int AMOUNT = 16;
		final String LOCKING_SCRIPT = "ABC";

		ITransactionFactory txFactory = new TransactionFactory();
		Transaction transaction = txFactory.generateCoinbasedTransaction(AMOUNT, LOCKING_SCRIPT);

		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.COINBASE);
	}

	// Approval:
	// inputs > 0;
	// outputs = 0 || > 0
	// approvals > 0
	@Test
	@Ignore // TODO will fail because generateApprovedTransaction is not
			// implemented yet.
	public void testWhichTransactionApproval() {

		final int AMOUNT = 16;
		final String HASH_PREVIOUS_OUTPUT = "ABC";
		final String LOCKING_SCRIPT = "ABC";
		final String OWNER_ADDRESS = "ABC";
		final String HOLDER_SIGNATURE = "ABC";

		ITransactionFactory txFactory = new TransactionFactory();
		Transaction transaction = txFactory.generateApprovedTransaction(AMOUNT, HOLDER_SIGNATURE, OWNER_ADDRESS,
				LOCKING_SCRIPT);

		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.APPROVED);
	}

	// Revoke:
	// inputs > 0;
	// outputs = 0 
	// approvals = null
	@Test
	@Ignore // TODO will fail because generateApprovedTransaction is not
			// implemented yet.
	public void testWhichTransactionRevoke() {

		final int AMOUNT = 16;
		final String LOCKING_SCRIPT = "ABC";

		ITransactionFactory txFactory = new TransactionFactory();
		Transaction transaction = txFactory.generateRevokeTransaction(AMOUNT, LOCKING_SCRIPT);

		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.REVOKE);
	}

	// Regular:
	// inputs > 0;
	// outputs > 0;
	// approvals = 0;
	@Test
	public void testWhichTransactionRegular() {

		final String unlockingScript = "ABC";
		final int AMOUNT = 16;

		List<Output> outputs = TxFactory.getRandomPreviousOutputs();
		ITransactionFactory txFactory = new TransactionFactory();
		Transaction transaction = txFactory.generateRegularTransaction(outputs, AMOUNT, unlockingScript);

		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.REGULAR);
	}

}
