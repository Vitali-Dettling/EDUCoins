package org.educoins.core.transaction;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.AssertTrue;

import org.apache.logging.log4j.core.tools.Generate;
import org.educoins.core.Block;
import org.educoins.core.Client;
import org.educoins.core.Wallet;
import org.educoins.core.transaction.Approval;
import org.educoins.core.transaction.Input;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.educoins.core.transaction.TransactionFactory;
import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.Generator;
import org.educoins.core.utils.TxFactory;

public class TransactionTest {

	@Test
	public void testApprovedTransaction(){
		Client client = new Client();
		
		int toApproveAmount = 1;
		String owner = Generator.getSecureRandomString256HEX();
		String lockingScript = Generator.getSecureRandomString256HEX();
		
		List<Output> outputs = TxFactory.getRandomPreviousOutputs();
		Block block = BlockStoreFactory.getRandomBlock();
		Transaction tx = BlockStoreFactory.generateTransaction(1);
		tx.setOutputs(outputs);
		block.addTransaction(tx);
		client.distructOwnOutputs(block);
		
		Transaction approvedTx = client.generateApprovedTransaction(toApproveAmount, owner, lockingScript);
		assertNotNull(approvedTx);
		assertTrue(!approvedTx.getApprovals().isEmpty());
		assertEquals(approvedTx.getApprovals().get(0).getAmount(), 1);
		String hashPreviousOutput = approvedTx.getApprovals().get(0).getHashPreviousOutput();
		boolean isTrue = false;
		for(Output out : outputs){
			if(hashPreviousOutput.equals(out.hash().toString()) ){
				isTrue = true;
				break;
			}
		}
		assertTrue(isTrue);
	}
	
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
	public void testWhichTransactionApproval() {

		final int AMOUNT = 16;
		final String HASH_PREVIOUS_OUTPUT = "ABC";
		final String LOCKING_SCRIPT = "ABC";
		final String OWNER_ADDRESS = "ABC";
		final String HOLDER_SIGNATURE = "ABC";
		
		List<Output> outputs = TxFactory.getRandomPreviousOutputs();
		
		ITransactionFactory txFactory = new TransactionFactory();
		Transaction transaction = txFactory.generateApprovedTransaction(outputs, AMOUNT, OWNER_ADDRESS, LOCKING_SCRIPT);

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
