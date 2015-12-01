package org.educoins.core.utils;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Gateway;
import org.educoins.core.IBlockReceiver;
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
	private static BlockChain mockedBlockchain;


	static {
		
		IBlockReceiver blockReceiver = Mockito.mock(IBlockReceiver.class);
		ITransactionReceiver txReceiver = Mockito.mock(ITransactionReceiver.class);
		ITransactionTransmitter txTransmitter = Mockito.mock(ITransactionTransmitter.class);
		Wallet mockedWallet = MockedWallet.getMockedWallet(); 
		mockedStore = MockedStore.getStore();
		
		mockedBlockchain = new BlockChain(blockReceiver, txReceiver, txTransmitter, mockedStore, mockedWallet);

	}
	
	public static IBlockStore getStore(){
		return mockedStore;
	}

	public static BlockChain getMockedBlockChain() {
		return mockedBlockchain;
	}

	public static void sendTransaction(Transaction transaction) {
		mockedBlockchain.transactionReceived(transaction);
	}
	
	public static void storeGateway(){
		Block block = BlockStoreFactory.getRandomBlock();
		Transaction tx = new Transaction();
		Gateway gateway = mockedBlockchain.getGateway();
		tx.addGateway(gateway);
		mockedBlockchain.transactionReceived(tx);
		block = mockedBlockchain.prepareNewBlock(block);		
		mockedBlockchain.foundPoW(block);
	}
	
	public static Block getLastStoredBlock(){
		Block latestBlock = mockedStore.getLatest();
		return mockedBlockchain.getPreviousBlock(latestBlock);
	}
	
	public static void close(){
		MockedWallet.delete();
		MockedStore.delete();
	}
	
	public static void delete(){
		MockedStore.delete();
		MockedWallet.delete();
	}

}
