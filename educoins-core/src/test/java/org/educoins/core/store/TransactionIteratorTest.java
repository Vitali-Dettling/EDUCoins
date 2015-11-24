/**
 * 
 */
package org.educoins.core.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.Input;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.utils.BlockStoreFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Vitali Dettling
 *
 */
public class TransactionIteratorTest {

	private IBlockStore store;
	
	/**
	 * {@link org.educoins.core.store.TransactionIterator#TransactionIterator(org.educoins.core.store.IBlockStore, byte[])}
	 * .
	 */
    @Before
    public void setup() {
		try {
			
			this.store = BlockStoreFactory.getBlockStore();
			
		} catch (BlockStoreException e) {
			fail();
		}

	}

	/**
	 * Test method for getting the previous output of an input.
	 * {@link org.educoins.core.store.TransactionIterator#previous(org.educoins.core.Input)}
	 * .
	 */
	@Test
	public void testPreviousSameBlock() {
		
		List<Transaction> txList = BlockStoreFactory.getConnectedTransactions();
		
		Block block = BlockStoreFactory.getRandomBlock();
		block.setTransactions(txList);
		
		this.store.put(block);
				
		ITransactionIterator iterator = this.store.transactionIterator();
		
		Input input = txList.get(1).getInputs().get(0);
		Output out = iterator.previous(input);
		
		assertNotNull(out);
		byte[] org = input.getHashPrevOutput().getBytes();
		byte[] result = out.getConcatedOutput();
		
		assertEquals(org.length, result.length); 
		Assert.assertArrayEquals(org, result); 
		
		
	}

}
