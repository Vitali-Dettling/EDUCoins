package org.educoins.core;

import org.educoins.core.store.BlockStoreException;
import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.Sha256Hash;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

public class BlockChainTest {
	
	private BlockChain mockedBlockchain;
	 
	@Before
	public void setUp() throws BlockStoreException {
		this.mockedBlockchain = new BlockChain(mock(IBlockReceiver.class),
				mock(ITransactionReceiver.class),
				mock(ITransactionTransmitter.class),
				BlockStoreFactory.getBlockStore());
	}
	
	@Test
	public void testPrepareNewBlock() {
		Block lastBlock = new Block();
		Block newBlock = this.mockedBlockchain.prepareNewBlock(lastBlock);
		
		assertEquals(newBlock.getVersion(), lastBlock.getVersion());
		assertNotEquals(newBlock.getHashMerkleRoot(), Sha256Hash.ZERO_HASH);
		assertEquals(newBlock.getHashPrevBlock(), lastBlock.hash());
		assertEquals(newBlock.getTransactionsCount(), 1);
		assertEquals(newBlock.getBits().compareTo(lastBlock.getBits()), 0);
		
	}

	@Test
	public void testCalcNewDifficulty() throws Exception {
		Sha256Hash oldDifficulty = Sha256Hash.wrap("affeaffeaffe00000000");
		Sha256Hash newDifficulty = BlockChain.calcNewDifficulty(oldDifficulty, 80*10*1000, 20*10*1000);
		Assert.assertEquals(oldDifficulty.toBigInteger(), newDifficulty.toBigInteger());
		Assert.assertEquals(oldDifficulty.compareTo(newDifficulty), 0);
	}

}
