package org.educoins.core;

import org.educoins.core.Approval;
import org.educoins.core.Input;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.Transaction.ETransaction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransactionTest {
	
			// Coinbase:
			// inputs = 0;
			// outputs > 0;
			// approvals = 0;
	@Test
	public void testWhichTransactionCoinbase() {
	
		final int AMOUNT = 16;
		final String LOCKING_SCRIPT = "ABC";
		
		Transaction transaction = new Transaction();
		
		Output output = new Output(AMOUNT, LOCKING_SCRIPT);
		transaction.addOutput(output);
		
		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.COINBASE);
	}
	
			// Approval:
			// inputs > 0;
			// outputs = 0 || > 0
			// approvals > 0
	@Test	//Test with: output = 0
	public void testWhichTransactionApprovalWithoutOutput() {
	
		final int AMOUNT = 16;
		final String unlockingScript = "ABC";
		final String HASH_PREVIOUS_OUTPUT= "ABC";
		final String LOCKING_SCRIPT = "ABC";
		final String OWNER_ADDRESS= "ABC";
		final String HOLDER_SIGNATURE= "ABC";
		
		Transaction transaction = new Transaction();
		
		Input input = new Input(AMOUNT, HASH_PREVIOUS_OUTPUT, unlockingScript);
		transaction.addInput(input);

		Approval approval = new Approval(AMOUNT, OWNER_ADDRESS, HOLDER_SIGNATURE, LOCKING_SCRIPT);
		transaction.addApproval(approval);
		
		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.APPROVED);
	}
	
			// Approval:
			// inputs > 0;
			// outputs = 0 || > 0
			// approvals > 0
	@Test	//Test with: output > 0
	public void testWhichTransactionApprovalWithOutput() {
		
		final String unlockingScript = "ABC";
		final int AMOUNT = 16;
		final String HASH_PREVIOUS_OUTPUT= "ABC";
		final String LOCKING_SCRIPT = "ABC";
		final String OWNER_ADDRESS= "ABC";
		final String HOLDER_SIGNATURE= "ABC";
		
		Transaction transaction = new Transaction();
		
		Input input = new Input(AMOUNT, HASH_PREVIOUS_OUTPUT, unlockingScript);
		transaction.addInput(input);
		
		Output output = new Output(AMOUNT, LOCKING_SCRIPT);
		transaction.addOutput(output);
		
		Approval approval = new Approval(AMOUNT, OWNER_ADDRESS, HOLDER_SIGNATURE, LOCKING_SCRIPT);
		transaction.addApproval(approval);
		
		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.APPROVED);
	}
	
			// Regular:
			// inputs > 0;
			// outputs > 0;
			// approvals = 0;
	@Test
	public void testWhichTransactionRegular() {
	
		final String unlockingScript = "ABC";
		final int AMOUNT = 16;
		final String LOCKING_SCRIPT = "ABC";
		final String HASH_PREVIOUS_OUTPUT= "ABC";
		
		Transaction transaction = new Transaction();
		
		Input input = new Input(AMOUNT, HASH_PREVIOUS_OUTPUT, unlockingScript);
		transaction.addInput(input);
		
		Output output = new Output(AMOUNT, LOCKING_SCRIPT);
		transaction.addOutput(output);
		
		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.REGULAR);
	}

	
	

}
