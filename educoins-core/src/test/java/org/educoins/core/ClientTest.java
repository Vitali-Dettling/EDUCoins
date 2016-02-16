package org.educoins.core;

import java.util.List;

import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.educoins.core.utils.AppConfigInitializer;
import org.educoins.core.utils.MockedClient;
import org.educoins.core.utils.MockedWallet;
import org.educoins.core.utils.Sha256Hash;
import org.junit.*;

public class ClientTest {


	@Before
	public void init() {
		AppConfigInitializer.init();
	}

	@After
	public void deleteTmp() {
		MockedClient.delete();
	}
	
	@Test
	@Ignore
	public void testReceivingApprovedTransaction() {

		Client mockedClient = MockedClient.getClient();

		Transaction approvedTx = MockedClient.generateApprovedTransaction(null);

		Block block = new Block();
		block = BlockStoreFactory.getRandomBlock(block);
		block.addTransaction(approvedTx);
		mockedClient.ownTransactions(block);

		int amountResult = mockedClient.getApprovedCoins();
		int amountExpected = approvedTx.getApprovals().get(0).getAmount();

		Assert.assertEquals(amountExpected, amountResult);

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
		String lockingScript = MockedWallet.getPublicKey();
		List<Transaction> reqeived = MockedClient.sendApprovedTransaction(1, owner, lockingScript);
		checkTransactionType(reqeived, ETransaction.APPROVED);
	}

	@Test
	@Ignore //TODO is not implemented yet.
	public void testSendRevokeTransaction() {

		String lockingScript = MockedWallet.getPublicKey();
		List<Transaction> reqeived = MockedClient.sendRevokedTransaction(Sha256Hash.wrap("123"), lockingScript);
		checkTransactionType(reqeived, ETransaction.REVOKE);
	}

	private void checkTransactionType(List<Transaction> reqeived, ETransaction type) {
		Transaction tx = reqeived.get(reqeived.size() - 1);
		ETransaction txType = tx.whichTransaction();
		reqeived = null;
		Assert.assertTrue(txType == type);
	}

}
