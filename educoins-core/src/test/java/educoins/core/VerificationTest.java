package educoins.core;

import static org.junit.Assert.assertTrue;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Verification;
import org.educoins.core.Wallet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import educoins.core.utils.MockedBlockChain;

public class VerificationTest {
	
	private Verification verification;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp(){
		
		Wallet wallet = new Wallet();
		MockedBlockChain mockedBlockChain = new MockedBlockChain();
		BlockChain blockChain = mockedBlockChain.getMockedBlockChain();
		this.verification = new Verification(wallet, blockChain);
	}

	@Test
	public void testVerifyBlock() {
		
		//Check null.
		Block block = null;
		thrown.expect(NullPointerException.class);						
		this.verification.verifyBlock(block);
		
		//Check Genesis block.
		block = new Block();
		assertTrue(this.verification.verifyBlock(block));
		
	}
	
	/*
	    block.setBits(bits);
		block.setHashMerkleRoot(hashMerkleRoot);
		block.setHashPrevBlock(hashPrevBlock);
		block.setNonce(nonce);
		block.setTime(time);
		block.setVersion(version);
		block.setTransactions(transactions);
	 * */

}
