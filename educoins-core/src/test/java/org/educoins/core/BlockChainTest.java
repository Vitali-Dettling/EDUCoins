/**
 * 
 */
package org.educoins.core;

import static org.junit.Assert.*;

import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.MockedBlockChain;
import org.educoins.core.utils.MockedStore;
import org.junit.AfterClass;
import org.junit.Test;

import junit.framework.Assert;

/**
 * @author Vitali Dettling
 *
 */
public class BlockChainTest {
	
	
	@AfterClass
	public static void deleteTmp(){
		MockedBlockChain.close();
	}
	
	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#BlockChain(org.educoins.core.IBlockReceiver, org.educoins.core.ITransactionReceiver, org.educoins.core.ITransactionTransmitter, org.educoins.core.store.IBlockStore)}
	 * .
	 */
	@Test
	public void testBlockChain() {

	}

	/**
	 * Test method for {@link org.educoins.core.BlockChain#getWallet()}.
	 */
	@Test
	public void testGetWallet() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#addBlockListener(org.educoins.core.IBlockListener)}
	 * .
	 */
	@Test
	public void testAddBlockListener() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#removeBlockListener(org.educoins.core.IBlockListener)}
	 * .
	 */
	@Test
	public void testRemoveBlockListener() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#notifyBlockReceived(org.educoins.core.Block)}
	 * .
	 */
	@Test
	public void testNotifyBlockReceived() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#blockReceived(org.educoins.core.Block)}
	 * .
	 */
	@Test
	public void testBlockReceived() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#addTransactionListener(org.educoins.core.ITransactionListener)}
	 * .
	 */
	@Test
	public void testAddTransactionListener() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#removeTransactionListener(org.educoins.core.ITransactionListener)}
	 * .
	 */
	@Test
	public void testRemoveTransactionListener() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#notifyTransactionReceived(org.educoins.core.Transaction)}
	 * .
	 */
	@Test
	public void testNotifyTransactionReceived() {

	}

	/**
	 * Test method is successful if runs without throwing any exceptions.
	 * 
	 * {@link org.educoins.core.BlockChain#sendTransaction(org.educoins.core.Transaction)}
	 * .
	 */
	@Test
	public void testSendTransactionGateway() {

		Block block = new Block();

		//Create an chain of Gateways.
		for (int i = 0; i < 10; i++) {
			block = BlockStoreFactory.generateGatewayChain(block);
			MockedStore.put(block);
		}

		//Signed and send by different external Gateways.
		for (int i = 0; i < 10; i++) {
			Gate gate = BlockStoreFactory.generateExternSignedGate();

			Transaction tx = new Transaction();
			tx.setGate(gate);
			
			MockedBlockChain.sendTransaction(tx);			
		}
		MockedBlockChain.storeGateway();
		Block latestBlock = MockedBlockChain.getLastStoredBlock();
		Gateway gateway = latestBlock.getTransactions().get(0).getGateways().get(0);
		
		//Gateway was created and stored in the Blockchain.
		assertNotNull(gateway);
		
		int result = latestBlock.getTransactions().get(0).getGateways().get(0).getGates().size();
		int expexted = 7;
		assertEquals(expexted, result);
	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#transactionReceived(org.educoins.core.Transaction)}
	 * .
	 */
	@Test
	public void testTransactionReceived() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#foundPoW(org.educoins.core.Block)}.
	 */
	@Test
	public void testFoundPoW() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#prepareNewBlock(org.educoins.core.Block)}
	 * .
	 */
	@Test
	public void testPrepareNewBlock() {

	}

	/**
	 * Test method for
	 * {@link org.educoins.core.BlockChain#getPreviousBlock(org.educoins.core.Block)}
	 * .
	 */
	@Test
	public void testGetPreviousBlock() {

	}

}
