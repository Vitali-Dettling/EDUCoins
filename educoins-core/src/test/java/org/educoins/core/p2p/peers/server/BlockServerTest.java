package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.utils.RestClient;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URI;

import static org.junit.Assert.*;

/**
 * Tests the {@link BlockServer}.
 * Created by typus on 11/5/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(BlockServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8080")
public class BlockServerTest {

    @Autowired
    protected IBlockStore store;

    protected RestClient<Block[]> restClient;
    protected int port = 8080;
    protected String blocksResourcePath = "http://localhost:" + port + BlockServer.BLOCKS_RESOURCE_PATH;
    protected String blockHeadersResourcePath = "http://localhost:" + port + BlockServer.BLOCK_HEADERS_RESOURCE_PATH;

    @Before
    public void setup() {
        try {
            store.iterator().hasNext();
        } catch (IllegalArgumentException e) {
            BlockStoreFactory.fillRandomTree(store);
        }

        restClient = new RestClient<>();
    }

    @After
    public void tearDown() {
//        try {
//            store.destroy();
//        } catch (BlockStoreException e) {
//            throw new IllegalStateException("Db could not be deleted!");
//        }
//        if (!IO.deleteDefaultBlockStoreFile())
//            throw new IllegalStateException("Db could not be deleted!");
    }

    @Test
    public void testStart() throws Exception {
        assertNotNull(restClient.get(URI.create(blocksResourcePath), Block[].class));
        assertNotNull(restClient.get(URI.create(blockHeadersResourcePath), Block[].class));
    }
}