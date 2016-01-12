package org.educoins.core.utils;

import static org.mockito.Mockito.times;

import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.Wallet;
import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.transaction.Input;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.RegularTransaction;
import org.educoins.core.transaction.Transaction;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;

public class MockedClient {

	@Spy
	private static BlockChain mockedBlockchain;

	private static int count = 0;
	private static List<Output> outputs;
	private static Client mockedClient;
	private static ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);

	static {

		mockedBlockchain = Mockito.spy(MockedBlockChain.getMockedBlockChain());
		mockedClient = new Client();
	}

	public static Client getClient() {
		return mockedClient;
	}

	private static List<Transaction> receivedTransaction() {

		Mockito.verify(mockedBlockchain, times(++count)).sendTransaction(txCaptor.capture());
		return txCaptor.getAllValues();
	}

	//TODO Late, if the gateways will be implemented.
//	public static List<Transaction> sendGateTransaction(String publicKey) {
//		mockedClient.sendGateTransaction(publicKey);
//		return receivedTransaction();
//	}

	public static List<Transaction> sendRegularTransaction(int amount, String lockingScript) {
		transactionReceived();
		Block block = BlockStoreFactory.getRandomBlock();
		Transaction tx = BlockStoreFactory.generateTransaction(1);
		block.addTransaction(tx);
		mockedClient.distructOwnOutputs(block);
		Transaction transaction = mockedClient.generateRegularTransaction(amount, lockingScript);
		mockedBlockchain.sendTransaction(transaction);
		return receivedTransaction();
	}

	public static List<Transaction> sendApprovedTransaction(int amount, String owner, String lockingScript) {
		transactionReceived();
		Transaction transaction = mockedClient.generateApprovedTransaction(amount, owner, owner, lockingScript);
		mockedBlockchain.sendTransaction(transaction);
		return receivedTransaction();
	}
	
	public static List<Transaction> sendRevokedTransaction(int amount, String lockingScript) {
		transactionReceived();
		Transaction transaction = mockedClient.generateRevokeTransaction(amount, lockingScript);
		mockedBlockchain.sendTransaction(transaction);
		return receivedTransaction();
	}

	public static List<Transaction> sendRevokeTransaction(String publicKey) {
		//TDOD [Vitali] Is about to come. 
		return receivedTransaction();
	}
	
	private static void transactionReceived(){
		Input input = BlockStoreFactory.generateRandomInput(MockedWallet.getPublicKey());
		Transaction tx = BlockStoreFactory.generateTransaction(1);
		Output out = tx.getOutputs().get(tx.getOutputsCount() - 1);
		tx.addInput(input);
		tx.addOutput(out);
		Block block = new Block();
		block.addTransaction(tx);
		mockedClient.distructOwnOutputs(block);
	}
	
	public static Transaction generateApprovedTransaction(String lockingScript){
		Client client = MockedClient.getClient();
		
		int toApproveAmount = 1;
		String owner = Generator.getSecureRandomString256HEX();
		if(lockingScript == null){
			lockingScript = MockedWallet.getPublicKey();
		}
		
		MockedClient.outputs = TxFactory.getRandomPreviousOutputs();
		Block block = BlockStoreFactory.getRandomBlock();
		Transaction tx = BlockStoreFactory.generateTransaction(1);
		tx.setOutputs(MockedClient.outputs);
		block.addTransaction(tx);
		client.distructOwnOutputs(block);
		
		return client.generateApprovedTransaction(toApproveAmount, owner, owner, lockingScript);
	}
	
	
	public static List<Output> getOutputs(){
		return MockedClient.outputs;
	}
	
	public static void delete(){
		MockedWallet.delete();
		MockedBlockChain.delete();
	}
}
