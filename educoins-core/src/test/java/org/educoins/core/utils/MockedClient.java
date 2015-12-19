package org.educoins.core.utils;

import static org.mockito.Mockito.times;

import java.util.List;

import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.Input;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.Wallet;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;

public class MockedClient {

	@Spy
	private static BlockChain mockedBlockchain;

	private static int count = 0;
	private static Client mockedClient;
	private static ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);

	static {

		mockedBlockchain = Mockito.spy(MockedBlockChain.getMockedBlockChain());
		Wallet mockedWallet = MockedWallet.getMockedWallet();
		mockedClient = new Client(mockedWallet);
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

	public static List<Transaction> sendRegularTransaction(int amount, String dstPublicKey, String lockingScript) {
		transactionReceived();
		//mockedClient.sendRegularTransaction(amount, dstPublicKey, lockingScript);
		return receivedTransaction();
	}

	public static List<Transaction> sendApprovedTransaction(int amount, String owner, String holder, String lockingScript) {
		transactionReceived();
		//mockedClient.sendApprovedTransaction(amount, owner, holder, lockingScript);
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
		String dstPublicKey = input.getHashPrevOutput();
		tx.addInput(input);
		tx.addOutput(out);
		//mockedClient.transactionReceived(tx);
	}
	
	public static void delete(){
		MockedBlockChain.delete();
	}
}
