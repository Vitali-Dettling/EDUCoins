package org.educoins.core.p2p.peers;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.Wallet;
import org.educoins.core.p2p.peers.server.PeerServer;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.transaction.CoinbaseTransaction;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.MockedBlockChain;
import org.educoins.core.utils.MockedStore;
import org.educoins.core.utils.MockedWallet;
import org.educoins.core.utils.RestClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
    /**
     * Submit an empty transaction. Behavior is undefined so far.
     */
    @Ignore
    @Test
    public void testSubmitEmpty() throws IOException {
        Transaction tx = new CoinbaseTransaction(2, "ABC");
        restClient.post(TRANSACTION_URI, tx);
        //TODO: Test real error case
    }
    
    @Test 
    public void testGetAmount(){
		Client client = new Client();
		
		int expected = 0;
		Block block = new Block();
		for (int i = 0; i < 10; i++) {
			block = BlockStoreFactory.getRandomBlock(block);
			String publicKey = MockedWallet.getPublicKey();
			Output out = new Output(6, publicKey);
			expected += 6;
			Transaction tx = new CoinbaseTransaction(2, "ABC");
			tx.addOutput(out);
			block.addTransaction(tx);
			client.distructOwnOutputs(block);
		}

		int result = 0;
		for(int i = 0 ; i < 2 ; i++){
			result = client.getAmount();
		}
		
		Assert.assertEquals(expected, result);
    	
    }
    
}