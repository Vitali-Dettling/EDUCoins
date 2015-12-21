package org.educoins.core.utils;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.ITransactionReceiver;
import org.educoins.core.ITransactionTransmitter;
import org.educoins.core.Miner;
import org.educoins.core.Transaction;
import org.educoins.core.Wallet;
import org.educoins.core.p2p.peers.HttpProxyPeerGroup;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockStore;
import org.mockito.Mock;
import org.mockito.Mockito;

public class MockedBlockChain {

	private static IBlockStore mockedStore;
	private static BlockChain mockedBlockchain;


	static {
		
		mockedStore = MockedStore.getStore();
		Wallet mockedWallet = MockedWallet.getMockedWallet();
		IProxyPeerGroup remoteProxy = new HttpProxyPeerGroup();
		IBlockStore store = MockedStore.getStore();
		mockedBlockchain = new BlockChain(remoteProxy, mockedWallet, store);

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
		//TODO Late, if the gateways will be implemented.
//		Block block = BlockStoreFactory.getRandomBlock();
//		Transaction tx = new Transaction();
//		Gateway gateway = mockedBlockchain.getGateway();
//		tx.addGateway(gateway);
//		mockedBlockchain.transactionReceived(tx);
//		block = mockedBlockchain.prepareNewBlock(block);		
//		mockedBlockchain.foundPoW(block);
	}
	
	public static Block getLastStoredBlock() {
		Block latestBlock = mockedStore.getLatest();
		try {
			return mockedBlockchain.getPreviousBlock(latestBlock);
		} catch (BlockNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return latestBlock;
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
