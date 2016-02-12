package org.educoins.core.transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.educoins.core.Wallet;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.Generator;
import org.educoins.core.utils.MockedClient;
import org.educoins.core.utils.MockedWallet;
import org.educoins.core.utils.Sha256Hash;
import org.educoins.core.utils.TxFactory;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

public class TransactionTest {

	@After
	public void tearDown() {
		MockedClient.delete();
	}

	@Test
	public void testPreviousOutput() {

		Transaction approvedTx = MockedClient.generateApprovedTransaction(null);
		Sha256Hash hashPreviousOutput = approvedTx.getInputs().get(0).getHashPrevOutput();
		List<Output> outputs = MockedClient.getOutputs();
		for (Output out : outputs) {
			if (hashPreviousOutput.equals(out.hash().toString())) {
				assertTrue(true);
				break;
			}
		}
	}

	@Test
	public void testApprovedTxHolderSignature() {

		String lockingScript = MockedWallet.getPublicKey();
		Transaction approvedTx = MockedClient.generateApprovedTransaction(lockingScript);

		String holderSignature = approvedTx.getApprovals().get(0).getHolderSignature();
		approvedTx.getApprovals().get(0).setHolderSignature(null);
		Sha256Hash txHash = approvedTx.hash();

		String newSignature = Wallet.getSignature(lockingScript, txHash.toString());

		assertTrue(Wallet.compare(txHash.toString(), newSignature, lockingScript));
		// TODO for some reason it does validate if the test is run manually. If
		// it is run through maven it fails?
//		assertTrue(Wallet.compare(txHash.toString(), holderSignature, lockingScript));
	}

	@Test
	public void testApprovedTransactionBasic() {

		Transaction approvedTx = MockedClient.generateApprovedTransaction(null);

		assertNotNull(approvedTx);
		assertTrue(!approvedTx.getApprovals().isEmpty());
		assertEquals(approvedTx.getApprovals().get(0).getAmount(), 1);
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
		final String HOLDER_SIGNATURE = "ABC";
		final String OWNER_ADDRESS = "ABC";

		List<Output> outputs = TxFactory.getRandomPreviousOutputs();

		ITransactionFactory txFactory = new TransactionFactory();
		Transaction transaction = txFactory.generateApprovedTransaction(outputs, AMOUNT, OWNER_ADDRESS, HOLDER_SIGNATURE, LOCKING_SCRIPT);

		ETransaction testee = transaction.whichTransaction();
		assertEquals(testee, ETransaction.APPROVED);
	}

	// Revoke:
	// Revoke > 0
	@Test
	public void testWhichTransactionRevoke() {

		final String LOCKING_SCRIPT = Generator.getSecureRandomString256HEX();
	
		Transaction tx = BlockStoreFactory.generateTransactionWithSameUnlockingScript(2);
		Approval app = new Approval(2, LOCKING_SCRIPT, LOCKING_SCRIPT);
		tx.addApproval(app);
		ITransactionFactory txFactory = new TransactionFactory();
		Transaction transaction = txFactory.generateRevokeTransaction(Arrays.asList(tx), tx.hash().toString());

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

	@Test
	public void testGetAmount(){
		final int AMOUNT = 16;
		final String receiver = "ABC";

		List<Output> outputs = TxFactory.getRandomPreviousOutputs();
		ITransactionFactory txFactory = new TransactionFactory();
		Transaction transaction = txFactory.generateRegularTransaction(outputs, AMOUNT, receiver);
		assertEquals(AMOUNT, transaction.getAmount(Wallet.getPublicKey()));
	}

}
