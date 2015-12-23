package org.educoins.core;

import java.util.List;

import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.educoins.core.utils.MockedClient;
import org.educoins.core.utils.MockedWallet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ClientTest {

	@After
	public void deleteTmp() {
		MockedClient.delete();
	}

	@Test
	public void testSendRegularTransaction() {

		String lockingScript = MockedWallet.getPublicKey();
		List<Transaction> reqeived = MockedClient.sendRegularTransaction(1, lockingScript);
		checkTransactionType(reqeived, ETransaction.REGULAR);
	}

	@Test
	@Ignore //TDOD approved transaction is not implemented yet.
	public void testSendApprovedTransaction() {

		String owner = MockedWallet.getPublicKey();
		String holder = MockedWallet.getPublicKey();
		String lockingScript = MockedWallet.getPublicKey();
		List<Transaction> reqeived = MockedClient.sendApprovedTransaction(1, owner, holder, lockingScript);
		checkTransactionType(reqeived, ETransaction.APPROVED);
	}
	
	@Test
	@Ignore //TDOD revoke transaction is not implemented yet.
	public void testSendRevokeTransaction() {

		String lockingScript = MockedWallet.getPublicKey();
		List<Transaction> reqeived = MockedClient.sendRevokedTransaction(1, lockingScript);
		checkTransactionType(reqeived, ETransaction.REVOKE);
	}

	@Test
	@Ignore //TODO no gateway implementation yet.
	public void testSendGateTransaction() {

//		String publicKey = MockedWallet.getPublicKey();
//		List<Transaction> reqeived = MockedClient.sendGateTransaction(publicKey);
//		checkTransactionType(reqeived, ETransaction.GATE);

	}

	private void checkTransactionType(List<Transaction> reqeived, ETransaction type) {
		Transaction tx = reqeived.get(reqeived.size() - 1);
		ETransaction txType = tx.whichTransaction();
		reqeived = null;
		Assert.assertTrue(txType == type);
	}

}
