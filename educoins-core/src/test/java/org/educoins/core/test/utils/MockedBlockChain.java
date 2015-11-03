package org.educoins.core.test.utils;

import org.educoins.core.BlockChain;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.ITransactionReceiver;
import org.educoins.core.ITransactionTransmitter;
import org.educoins.core.store.IBlockStore;
import org.junit.Before;
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
	private IBlockStore store;
	
	private BlockChain blockchain;
	
	@Before
	public void setUp(){
	
		IBlockReceiver blockReceiver = Mockito.mock(IBlockReceiver.class);
		ITransactionReceiver txReceiver = Mockito.mock(ITransactionReceiver.class);
		ITransactionTransmitter txTransmitter = Mockito.mock(ITransactionTransmitter.class);
		IBlockStore store = Mockito.mock(IBlockStore.class);
		
		this.blockchain = new BlockChain(blockReceiver, txReceiver, txTransmitter, store);
		
	}
	
	public BlockChain getMockedBlockChain(){
		return this.blockchain;
	}
	
	


}
