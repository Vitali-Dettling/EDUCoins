package org.educoins.core.utils;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Gateway;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.ITransactionListener;
import org.educoins.core.ITransactionReceiver;
import org.educoins.core.ITransactionTransmitter;
import org.educoins.core.Transaction;
import org.educoins.core.Wallet;
import org.educoins.core.store.IBlockStore;
import org.mockito.Mock;
import org.mockito.Mockito;

public class MockedBlockChain {

	@Mock
	private IBlockReceiver mockedBlockReceiver;
	@Mock
	private ITransactionReceiver mockedTxReceiver;
	@Mock
	private ITransactionTransmitter mockedTxTransmitter;
	@Mock
	private ITransactionReceiver mockedGatewayReceiver;
	@Mock
	private ITransactionTransmitter mockedGatewayTransmitter;

	private static IBlockStore mockedStore;
	private static BlockChain blockchain;

	static {

		IBlockReceiver blockReceiver = Mockito.mock(IBlockReceiver.class);
		ITransactionReceiver txReceiver = Mockito.mock(ITransactionReceiver.class);
		ITransactionTransmitter txTransmitter = Mockito.mock(ITransactionTransmitter.class);
		Wallet mockedWallet = MockedWallet.getMockedWallet(); 
		mockedStore = MockedStore.getStore();
		
		blockchain = new BlockChain(blockReceiver, txReceiver, txTransmitter, mockedStore, mockedWallet);

	}

	public static BlockChain getMockedBlockChain() {
		return blockchain;
	}

	public static void sendTransaction(Transaction transaction) {
		blockchain.transactionReceived(transaction);
	}
	
	public static void storeGateway(){
		Gateway gateway = blockchain.getGateway();
		Block block = BlockStoreFactory.getRandomBlock();
		Transaction tx = BlockStoreFactory.generateTransaction(1);
		tx.addGateway(gateway);
		block.addTransaction(tx);
		blockchain.foundPoW(block);
	}
	
	public static Block getLastStoredBlock(){
		return mockedStore.getLatest();
	}
	
	public static void close(){
		MockedWallet.delete();
		MockedStore.delete();
	}

}
