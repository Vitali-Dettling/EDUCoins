package org.educoins.core;

import static org.junit.Assert.*;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.MockedBlockChain;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

public class BlockChainTest {
	
	
	private static final int NEGATIVE = 0;
	private static final int COINBASE_ONLY = 1;
	private static final String HASH_PREV_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";
	private static final String HASH_MERKLE_ROOT = "0000000000000000000000000000000000000000000000000000000000000000";
	private static final String BITS = "1dffffff";
	
	private BlockChain mockedBlockchain;
	 
	@Before
	public void setUp(){
		
		MockedBlockChain mockedBlockchain = new MockedBlockChain();
		this.mockedBlockchain = mockedBlockchain.getMockedBlockChain();
	}
	
	@Test
	public void prepareNewBlockTest() {

		
		Block lastBlock = new Block();
		Block newBlock = this.mockedBlockchain.prepareNewBlock(lastBlock);
		
		assertTrue(newBlock.getVersion() > NEGATIVE);
		
		byte[] arrayMerkleTree = ByteArray.convertFromString(newBlock.getHashMerkleRoot());
		int valueMerkleTree = ByteArray.convertToInt(arrayMerkleTree);
		assertTrue(valueMerkleTree > 0);
		
		assertEquals(newBlock.getHashPrevBlock(), HASH_PREV_BLOCK);
		assertEquals(newBlock.getHashMerkleRoot(), HASH_MERKLE_ROOT);
		assertEquals(newBlock.getTransactionsCount(), COINBASE_ONLY);
		assertEquals(newBlock.getBits(), BITS);
		
	}
	
	//TODO[Vitali] Write a test where the difficulty calculation kicks in. 
	
	//TODO[Vitali] Test for getPreviousBlock();
	

}
