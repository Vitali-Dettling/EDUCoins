package org.educoins.core.transaction;

import org.junit.After;
import org.junit.Before;
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
import org.educoins.core.utils.MockedClient;
import org.educoins.core.utils.MockedWallet;
import org.educoins.core.utils.Sha256Hash;
import org.educoins.core.utils.TxFactory;

public class TransactionTest {

	@After
	public void tearDown(){
		MockedClient.delete();
	}
	
	@Before
	public void setUp(){
		MockedClient.delete();
	}
	
	@Test
	public void testPreviousOutput(){
		
		Transaction approvedTx = MockedClient.generateApprovedTransaction(null);
		String hashPreviousOutput = approvedTx.getApprovals().get(0).getHashPreviousOutput();
		List<Output> outputs = MockedClient.getOutputs();
		for(Output out : outputs){
			if(hashPreviousOutput.equals(out.hash().toString()) ){
				assertTrue(true);
				break;
			}
		}
	}
	
	@Test
	public void testApprovedTxHolderSignature(){
		
		String lockingScript = MockedWallet.getPublicKey();
		Transaction approvedTx = MockedClient.generateApprovedTransaction(lockingScript);

		String holderSignature = approvedTx.getApprovals().get(0).getHolderSignature();
		approvedTx.getApprovals().get(0).setHolderSignature(null);
		Sha256Hash txHash = approvedTx.hash();
		
		String newSignature = Wallet.getSignature(lockingScript, txHash.toString());
		
		assertTrue(Wallet.compare(txHash.toString(), newSignature, lockingScript));
		assertTrue(Wallet.compare(txHash.toString(), holderSignature, lockingScript));	
	}
	
	@Test
	public void testApprovedTransactionBasic(){
		
		Transaction approvedTx = MockedClient.generateApprovedTransaction(null);
		
		assertNotNull(approvedTx);
		assertTrue(!approvedTx.getApprovals().isEmpty());
		assertEquals(approvedTx.getApprovals().get(0).getAmount(), 1);
		assertTrue(approvedTx.getApprovals().get(0).getHashPreviousOutput().length() > 0);
		assertTrue(approvedTx.getApprovals().get(0).getHolderSignature().length() > 0);
		assertTrue(approvedTx.getApprovals().get(0).getLockingScript().length() > 0);
		assertTrue(approvedTx.getApprovals().get(0).getOwnerAddress().length() > 0);
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
		final String LOCKING_SCRIPT = "ABC";
		final String OWNER_ADDRESS = "ABC";
		
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
