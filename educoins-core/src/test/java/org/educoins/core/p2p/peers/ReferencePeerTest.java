package org.educoins.core.p2p.peers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.Wallet;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.utils.MockedBlockChain;
import org.educoins.core.utils.MockedClient;
import org.educoins.core.utils.MockedStore;
import org.educoins.core.utils.MockedWallet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ReferencePeerTest {

	@Test
	public void testStart() {

	}

	@Test
	public void testStop() {

	}

	@Test
	public void testGetAmount() {

		BlockChain blockchain = MockedBlockChain.getMockedBlockChain();
		ReferencePeer peer = new ReferencePeer(blockchain);
		Wallet mockedWallet = MockedWallet.getMockedWallet();
		Client client = new Client(mockedWallet);

		int expected = 0;
		Block block = new Block();
		String publicKey = peer.getPubKey();
		for (int i = 0; i < 10; i++) {
			block = BlockStoreFactory.getRandomBlock(block);
			Output out = new Output(6, publicKey);
			expected += 6;
			Transaction tx = new Transaction();
			tx.addOutput(out);
			block.addTransaction(tx);
			MockedStore.put(block);
		}
		int result = client.getAmount();
		Assert.assertEquals(result, expected);
	}

}
