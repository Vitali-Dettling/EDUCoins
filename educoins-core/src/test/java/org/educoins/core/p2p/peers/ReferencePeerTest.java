package org.educoins.core.p2p.peers;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.transaction.Input;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.RegularTransaction;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.MockedBlockChain;
import org.educoins.core.utils.MockedClient;
import org.educoins.core.utils.TxFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ReferencePeerTest {

	@After
	public void tearDown(){
		MockedBlockChain.delete();
		MockedClient.delete();
	}
	
	@Test
	public void testGetAmount() {

		BlockChain blockchain = MockedBlockChain.getMockedBlockChain();
		ReferencePeer peer = new ReferencePeer(blockchain);
		
		MockedClient.resetClient();
		Client client = MockedClient.getClient();

		int expected = 0;
		Block block = new Block();
		String publicKey = peer.getPubKey();
		List<Output> outputs = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {			
			Output out = new Output(6, publicKey);
			expected += 6;
			outputs.add(out);
		}
		block = BlockStoreFactory.getRandomBlock(block);
		
		List<Input> inputs = TxFactory.getRandomPreviousInputs();
		
		Transaction tx = new RegularTransaction(outputs, inputs).create();
		block.addTransaction(tx);
		client.distructOwnOutputs(block);
	
		int result = client.getEDICoinsAmount();
		Assert.assertEquals(result, expected);
	}

}
