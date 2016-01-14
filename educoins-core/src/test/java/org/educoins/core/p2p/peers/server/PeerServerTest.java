package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.utils.RestClient;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URI;

import static org.junit.Assert.*;

/**
 * Tests the {@link PeerServer}.
 * Created by typus on 11/5/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PeerServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8080")
@ActiveProfiles("test")
public class PeerServerTest {

    @Autowired
    protected IBlockStore store;

    protected RestClient<Block[]> restClient;
    protected int port = 8080;
    protected String blocksResourcePath = "http://localhost:" + port + PeerServer.BLOCKS_RESOURCE_PATH;
    protected String blockHeadersResourcePath = "http://localhost:" + port + PeerServer.BLOCK_HEADERS_RESOURCE_PATH;

    @Before
    public void setup() {
        try {
            int cnt = 0;
            IBlockIterator iter = store.iterator();
            while (iter.hasNext())
                cnt++;

            if (cnt <= 1)
                BlockStoreFactory.fillRandomTree(store);
        } catch (IllegalArgumentException e) {
            BlockStoreFactory.fillRandomTree(store);
        }

        restClient = new RestClient<>();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testStart() throws Exception {
        assertNotNull(restClient.get(URI.create(blockHeadersResourcePath), Block[].class));
        assertNotNull(restClient.get(URI.create(blocksResourcePath), Block[].class));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        BlockStoreFactory.removeAllBlockStores();
    }
}