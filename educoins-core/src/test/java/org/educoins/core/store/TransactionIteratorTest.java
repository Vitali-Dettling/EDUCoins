/**
 * 
 */
package org.educoins.core.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.Input;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.MockedWallet;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Vitali Dettling
 *
 */
public class TransactionIteratorTest {

	private static IBlockStore store;

	@AfterClass
	public static void deleteTmp() {
		MockedWallet.delete();
		try {
			store.destroy();
		} catch (BlockStoreException e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void createTmp() {

		try {
			store = BlockStoreFactory.getBlockStore();
		} catch (BlockStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		store.put(block);

		ITransactionIterator iterator = store.transactionIterator();

		Input input = txList.get(1).getInputs().get(0);
		Output out = iterator.previous(input);

		assertNotNull(out);
		byte[] org = ByteArray.convertFromString(input.getHashPrevOutput());
		byte[] result = out.getConcatedOutput();

		assertEquals(org.length, result.length);
		Assert.assertArrayEquals(org, result);

	}

}
