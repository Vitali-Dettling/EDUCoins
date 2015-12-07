package org.educoins.core.p2p.peers;

import org.educoins.core.CoinbaseTransaction;
import org.educoins.core.Input;
import org.educoins.core.Transaction;
import org.educoins.core.p2p.peers.server.PeerServer;
import org.educoins.core.utils.RestClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.*;

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
        Transaction tx = new Transaction();
        restClient.post(TRANSACTION_URI, tx);
        //TODO: Test real error case
    }
}