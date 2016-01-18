package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.peers.server.PeerServer;
import org.educoins.core.transaction.*;
import org.educoins.core.utils.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.net.URI;

import static org.mockito.Mockito.*;

/**
 * Created by dacki on 07.12.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PeerServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8083")
public class SoloMinerPeerTest {
	private static final URI TRANSACTION_URI = URI.create("http://localhost:8083/transaction");

	private RestClient<Transaction> restClient = new RestClient<>();

	@After
	public void tearDown() {
		MockedBlockChain.delete();
		MockedClient.delete();
	}

	/**
	 * Submit an empty transaction. Behavior is undefined so far.
	 */
	@Ignore
	@Test
	public void testSubmitEmpty() throws IOException {
		Transaction tx = new CoinbaseTransaction(2, "ABC");
		restClient.post(TRANSACTION_URI, tx);
		// TODO: Test real error case
	}

	@Test
	public void testGetAmount() {
		BlockChain blockchain = MockedBlockChain.getMockedBlockChain();
		Sha256Hash ownPublicKey = AppConfig.getOwnPublicKey();
		SoloMinerPeer peer = new SoloMinerPeer(blockchain, mock(IProxyPeerGroup.class), ownPublicKey);
		MockedClient.resetClient();
		Client client = MockedClient.getClient();

		int expected = 0;
		String publicKey = ownPublicKey.toString();

		for (int i = 0; i < 10; i++) {
			Block block = new Block();
			Transaction tx = new CoinbaseTransaction(60, publicKey).create();
			for (Output out : tx.getOutputs()) {
				expected += out.getAmount();
			}
			block.addTransaction(tx);
			client.distructOwnOutputs(block);
		}

		int result = client.getEDICoinsAmount();
		Assert.assertEquals(expected, result);
	}
}