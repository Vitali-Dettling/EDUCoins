package org.educoins.core;

import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.utils.Sha256Hash;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerificationTest {
	
	private Verification verification;
	private BlockChain blockChain;
	private Wallet wallet;

	@Rule
    public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp(){
		this.wallet = mock(Wallet.class);
		this.blockChain = mock(BlockChain.class);
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

	@Test
	public void testVerifyBlock1() {
		Block block = new Block();
		Output output = new Output(2, "abc", "def");

		CoinbaseTransaction transaction = new CoinbaseTransaction();
		transaction.addOutput(output);
		block.addTransaction(transaction);

		assertTrue(this.verification.verifyBlock(block));
	}

	@Test
	public void testVerifyBlock2() throws BlockNotFoundException {
		Block block = new Block();
		block.setHashPrevBlock(Sha256Hash.MAX_HASH);
		Output output = new Output(2, "abc", "def");

		CoinbaseTransaction transaction = new CoinbaseTransaction();
		transaction.addOutput(output);
		block.addTransaction(transaction);

		when(blockChain.getPreviousBlock(block)).thenReturn(new Block());
		assertTrue(this.verification.verifyBlock(block));

		Block block1 = new Block();
		block1.setHashPrevBlock(block.hash());
		Input input1 = new Input(2, block.getTransactions().get(0).hash().toString(), 0);
		input1.setUnlockingScript(Input.EInputUnlockingScript.PUBLIC_KEY, "123");
		input1.setUnlockingScript(Input.EInputUnlockingScript.SIGNATURE, "affe");
		Output output1 = new Output(2, "abc", "def");

		Transaction transaction1 = new Transaction();
		transaction1.addOutput(output1);
		transaction1.addInput(input1);
		block1.addTransaction(transaction1);
		when(blockChain.getPreviousBlock(block1)).thenReturn(block);
		when(wallet.checkSignature(any(String.class), any(byte[].class))).thenReturn(true);
		assertTrue(this.verification.verifyBlock(block1));
	}

	@Test
	public void testVerifyBlockUseInvalidInput() throws BlockNotFoundException {
		Block block = new Block();
		Output output = new Output(2, "abc", "def");

		CoinbaseTransaction transaction = new CoinbaseTransaction();
		transaction.addOutput(output);
		block.addTransaction(transaction);

		assertTrue(this.verification.verifyBlock(block));

		Block block1 = new Block();
		block1.setHashPrevBlock(block.hash());
		Input input1 = new Input(2, Sha256Hash.ZERO_HASH.toString(), 0);
		input1.setUnlockingScript(Input.EInputUnlockingScript.SIGNATURE, "123");
		input1.setUnlockingScript(Input.EInputUnlockingScript.PUBLIC_KEY, "adadadad");
		Output output1 = new Output(2, "abc", "def");

		Transaction transaction1 = new Transaction();
		transaction1.addOutput(output1);
		transaction1.addInput(input1);
		block1.addTransaction(transaction1);
		when(blockChain.getPreviousBlock(block1)).thenReturn(block);
		assertFalse(this.verification.verifyBlock(block1));
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
