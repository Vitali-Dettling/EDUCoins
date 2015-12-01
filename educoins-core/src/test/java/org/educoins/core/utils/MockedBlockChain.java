package org.educoins.core.utils;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
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
	@Mock
	private IBlockStore store;

	private static BlockChain blockchain;

	static {

		IBlockReceiver blockReceiver = Mockito.mock(IBlockReceiver.class);
		ITransactionReceiver txReceiver = Mockito.mock(ITransactionReceiver.class);
		ITransactionTransmitter txTransmitter = Mockito.mock(ITransactionTransmitter.class);
		Wallet mockedWallet = MockedWallet.getMockedWallet(); 
		
		blockchain = new BlockChain(blockReceiver, txReceiver, txTransmitter, MockedStore.getStore(), mockedWallet);

	}

	public static BlockChain getMockedBlockChain() {
		return blockchain;
	}

	public static void sendTransaction(Transaction transaction) {
		blockchain.transactionReceived(transaction);
	}

	

}
