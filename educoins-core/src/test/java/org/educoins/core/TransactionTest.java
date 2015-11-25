package org.educoins.core;

import static org.junit.Assert.assertEquals;

import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.utils.Generator;
import org.educoins.core.utils.Sha256Hash;
import org.junit.Test;

public class TransactionTest {
	
			// Coinbase:
			// inputs = 0;
			// outputs > 0;
			// approvals = 0;
	@Test
	public void testWhichTransactionCoinbase() {
	
		final int AMOUNT = 16;
		final String DST_PUBLIC_KEY = Generator.getSecureRandomString256HEX();
		final String LOCKING_SCRIPT = Generator.getSecureRandomString256HEX();
		
		Transaction transaction = new Transaction();
		
		Output output = new Output(AMOUNT, Sha256Hash.wrap(DST_PUBLIC_KEY), Sha256Hash.wrap(LOCKING_SCRIPT));
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
		final int index = 0;
		final String HASH_PREVIOUS_OUTPUT= Generator.getSecureRandomString256HEX();
		final String LOCKING_SCRIPT = Generator.getSecureRandomString256HEX();
		final String OWNER_ADDRESS= Generator.getSecureRandomString256HEX();
		final String HOLDER_SIGNATURE= Generator.getSecureRandomString256HEX();
		
		Transaction transaction = new Transaction();
		
		Input input = new Input(AMOUNT, Sha256Hash.wrap(HASH_PREVIOUS_OUTPUT), index);
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
		
		final int INDEX = 0;
		final int AMOUNT = 16;
		final String DST_PUBLIC_KEY = Generator.getSecureRandomString256HEX();
		final String HASH_PREVIOUS_OUTPUT= Generator.getSecureRandomString256HEX();
		final String LOCKING_SCRIPT = Generator.getSecureRandomString256HEX();
		final String OWNER_ADDRESS= Generator.getSecureRandomString256HEX();
		final String HOLDER_SIGNATURE= Generator.getSecureRandomString256HEX();
		
		Transaction transaction = new Transaction();
		
		Input input = new Input(AMOUNT, Sha256Hash.wrap(HASH_PREVIOUS_OUTPUT), INDEX);
		transaction.addInput(input);
		
		Output output = new Output(AMOUNT, Sha256Hash.wrap(DST_PUBLIC_KEY), Sha256Hash.wrap(LOCKING_SCRIPT));
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
	
		final int INDEX = 0;
		final int AMOUNT = 16;
		final String DST_PUBLIC_KEY = Generator.getSecureRandomString256HEX();
		final String LOCKING_SCRIPT = Generator.getSecureRandomString256HEX();
		final String HASH_PREVIOUS_OUTPUT= Generator.getSecureRandomString256HEX();
		
		Transaction transaction = new Transaction();
		
		Input input = new Input(AMOUNT, Sha256Hash.wrap(HASH_PREVIOUS_OUTPUT), INDEX);
		transaction.addInput(input);
		
		Output output = new Output(AMOUNT, Sha256Hash.wrap(DST_PUBLIC_KEY), Sha256Hash.wrap(LOCKING_SCRIPT));
		transaction.addOutput(output);
		
		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.REGULAR);
	}

	
	

}
