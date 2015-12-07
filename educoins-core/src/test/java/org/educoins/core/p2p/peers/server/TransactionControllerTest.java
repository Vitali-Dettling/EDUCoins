package org.educoins.core.p2p.peers.server;

import org.educoins.core.BlockChain;
import org.educoins.core.Transaction;
import org.educoins.core.utils.RestClient;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;

/**
 * Created by dacki on 06.12.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PeerServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8082")
public class TransactionControllerTest {
    private static final URI TRANSACTION_URI = URI.create("http://localhost:8082/transaction");

    @Autowired
    private BlockChain bc = mock(BlockChain.class);
    private RestClient<Transaction> restClient = new RestClient<>();

    @BeforeClass
    public static void init() throws URISyntaxException {
    }

    @Ignore
    @Test
    public void testSubmitEmptyTransaction() throws IOException {
        Transaction tx = new Transaction();
        restClient.post(TRANSACTION_URI, tx);
        //TODO: Test real error case
    }
}