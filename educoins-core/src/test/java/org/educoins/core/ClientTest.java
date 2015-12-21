package org.educoins.core;

import java.util.List;

import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.utils.MockedClient;
import org.educoins.core.utils.MockedWallet;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ClientTest {

	@AfterClass
	public static void deleteTmp() {
		MockedClient.delete();
	}

	@Test
	public void testSendRegularTransaction() {

		String lockingScript = MockedWallet.getPublicKey();
		List<Transaction> reqeived = MockedClient.sendRegularTransaction(1, lockingScript);
		checkTransactionType(reqeived, ETransaction.REGULAR);
	}

	@Test
	public void testSendApprovedTransaction() {

		String owner = MockedWallet.getPublicKey();
		String holder = MockedWallet.getPublicKey();
		String lockingScript = MockedWallet.getPublicKey();
		List<Transaction> reqeived = MockedClient.sendApprovedTransaction(1, owner, holder, lockingScript);
		checkTransactionType(reqeived, ETransaction.APPROVED);
	}
	
	@Test
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
