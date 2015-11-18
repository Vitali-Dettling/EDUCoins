package educoins.core;

import org.educoins.core.BlockChain;
import org.junit.Before;
import org.junit.Test;

import educoins.core.utils.MockedBlockChain;

public class BlockChainTest {
	
	
	private static final int NEGATIVE = 0;
	private static final int COINBASE_ONLY = 1;
	
	private BlockChain mockedBlockchain;
	 
	@Before
	public void setUp(){
		
		MockedBlockChain mockedBlockchain = new MockedBlockChain();
		this.mockedBlockchain = mockedBlockchain.getMockedBlockChain();
	}
	
	@Test
	public void testPrepareNewBlock() {
		
		//TODO [Vitali] Comment in again.
//		Block lastBlock = new Block();
//		Block newBlock = this.mockedBlockchain.prepareNewBlock(lastBlock);
//		
//		assertTrue(newBlock.getVersion() > NEGATIVE);
//		
//		byte[] arrayMerkleTree = newBlock.getHashMerkleRoot().getBytes();
//		int valueMerkleTree = ByteArray.convertToInt(arrayMerkleTree);
//		assertTrue(valueMerkleTree > 0);
//		
//		assertEquals(newBlock.getHashPrevBlock(), Sha256Hash.ZERO_HASH);
//		assertEquals(newBlock.getHashMerkleRoot(), Sha256Hash.ZERO_HASH);
//		assertEquals(newBlock.getTransactionsCount(), COINBASE_ONLY);
//		assertEquals(newBlock.getBits(), Sha256Hash.MAX_HASH);
		
	}
	
	//TODO[Vitali] Write a test where the difficulty calculation kicks in. 
	
	//TODO[Vitali] Test for getPreviousBlock();
	

}
