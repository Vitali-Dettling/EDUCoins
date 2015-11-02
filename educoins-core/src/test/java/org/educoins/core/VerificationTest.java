package org.educoins.core;

import static org.junit.Assert.*;

import org.educoins.core.utils.MockedBlockChain;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
	public void verifyBlockTest() {
		
		//Check null.
		Block block = null;
		thrown.expect(NullPointerException.class);						
		this.verification.verifyBlock(block);
		
		//Check Genesis block.
		block = new Block();
		assertTrue(this.verification.verifyBlock(block));
		
		//TODO [Vitali] Finish tests.
		
		
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
