package org.educoins.core;

import static org.junit.Assert.assertTrue;

import org.educoins.core.utils.MockedBlockChain;
import org.educoins.core.utils.MockedWallet;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VerificationTest {
	
	private Verification verification;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp(){
		
		Wallet mockedWallet = MockedWallet.getMockedWallet();
		BlockChain mockedBlockchain = MockedBlockChain.getMockedBlockChain();
		this.verification = new Verification(mockedWallet, mockedBlockchain);
	}
	
	@After
	public void tearDown(){
		MockedWallet.delete();
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
