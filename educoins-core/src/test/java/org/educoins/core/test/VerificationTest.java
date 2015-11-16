package org.educoins.core.test;

import static org.junit.Assert.assertTrue;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Gate;
import org.educoins.core.Transaction;
import org.educoins.core.Verification;
import org.educoins.core.Wallet;
import org.educoins.core.test.utils.MockedBlockChain;
import org.educoins.core.utils.ByteArray;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VerificationTest {
	
	private static final int HEX = 16;
	
	private Verification verification;
	private Wallet wallet;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp(){
		
		this.wallet = new Wallet();
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
	
	@Test
	public void testVerifyGate() {
		
		final String publicKey = this.wallet.getPublicKey();

		Transaction transaction = new Transaction();
		transaction.setVersion(1);
		
		Gate gate = new Gate(null, publicKey);
		transaction.setGate(gate);
		
		byte[] hash = transaction.hash();
		String hashedTranscation = ByteArray.convertToString(hash, HEX);
		
		String signature = this.wallet.getSignature(publicKey, hashedTranscation);
		gate.setSignature(signature);
		
		boolean testResult = this.verification.verifyGate(transaction);
		assertTrue(testResult);
	}
	

}
