package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.config.AppConfig;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.transaction.*;
import org.educoins.core.utils.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class ReferencePeerTest {

	@After
	public void tearDown(){
		MockedBlockChain.delete();
		MockedClient.delete();
	}
	
	@Ignore
	@Test
	public void testGetAmount() {

		BlockChain blockchain = MockedBlockChain.getMockedBlockChain();
		Sha256Hash ownPublicKey = AppConfig.getOwnPublicKey();
		ReferencePeer peer = new ReferencePeer(blockchain, mock(IProxyPeerGroup.class), ownPublicKey);
		Client client = MockedClient.getClient();

		int expected = 0;
		Block block = new Block();
		List<Output> outputs = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
			Output out = new Output(6, ownPublicKey.toString());
			expected += 6;
			outputs.add(out);
		}
		block = BlockStoreFactory.getRandomBlock(block);
		Transaction tx = new RegularTransaction(outputs, expected, expected, ownPublicKey.toString()).create();
		block.addTransaction(tx);
		client.distructOwnOutputs(block);
	
		int result = client.getEDICoinsAmount();
		Assert.assertEquals(result, expected);
	}

}
