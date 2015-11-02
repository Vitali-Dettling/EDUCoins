package org.educoins.core.utils;

import org.educoins.core.BlockChain;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.IBlockTransmitter;
import org.educoins.core.ITransactionReceiver;
import org.educoins.core.ITransactionTransmitter;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;

public class MockedBlockChain {
	
	@Mock
	IBlockTransmitter mockedBlockTransmitter;
	IBlockReceiver mockedBlockReceiver;
	ITransactionReceiver mockedTxReceiver;
	ITransactionTransmitter mockedTxTransmitter;
	
	private BlockChain blockchain;
	
	@Before
	public void setUp(){
		
		IBlockTransmitter blockTransmitter = Mockito.mock(IBlockTransmitter.class);
		IBlockReceiver blockReceiver = Mockito.mock(IBlockReceiver.class);
		ITransactionReceiver txReceiver = Mockito.mock(ITransactionReceiver.class);
		ITransactionTransmitter txTransmitter = Mockito.mock(ITransactionTransmitter.class);
		
		this.blockchain = new BlockChain(blockReceiver, blockTransmitter, txReceiver, txTransmitter);
		
	}
	
	public BlockChain getMockedBlockChain(){
		return this.blockchain;
	}
	
	


}
