package org.educoins.core;

import java.util.ArrayList;

import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.utils.MockedBlockChain;
import org.educoins.core.utils.Sha256Hash;
import org.educoins.core.BlockChain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BlockChainTest {

	private BlockChain mockedBlockchain;

	@Before
	public void setUp() {
		this.mockedBlockchain = MockedBlockChain.getMockedBlockChain();
	}

	@Test
	public void testGetBlocksFromIncludsGenesisBlock() throws BlockNotFoundException {

		IBlockStore store = this.mockedBlockchain.getBlockStore();
		Block genesisBlock = new Block();
		BlockStoreFactory.fillRandomTree(store);

		this.mockedBlockchain.setBlockStore(store);
		ArrayList<Block> blocks = (ArrayList<Block>) this.mockedBlockchain.getBlocksFrom(genesisBlock.hash());
		String expecteds = genesisBlock.toString();
		String actuals = blocks.get(0).toString();
		Assert.assertEquals(expecteds, actuals);
	}

	@Test
	public void testCalcNewDifficulty() throws Exception {
		Sha256Hash oldDifficulty = Sha256Hash.wrap("affeaffeaffe00000000");
		Sha256Hash newDifficulty = BlockChain.calcNewDifficulty(oldDifficulty, 80 * 10 * 1000, 20 * 10 * 1000);
		Assert.assertEquals(oldDifficulty.toBigInteger(), newDifficulty.toBigInteger());
		Assert.assertEquals(oldDifficulty.compareTo(newDifficulty), 0);
	}

}
