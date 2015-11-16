package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.utils.IO;
import org.educoins.core.utils.RestClient;
import org.junit.*;

import java.net.URI;

import static org.junit.Assert.*;

/**
 * Tests the {@link BlockServer}.
 * Created by typus on 11/5/15.
 */
public class BlockServerTest {
    protected IBlockStore store;
    protected RestClient<Block[]> restClient;
    protected int port = 8090;
    protected String blocksResourcePath = "http://localhost:" + port + BlockServer.BLOCKS_RESOURCE_PATH;
    protected String blockHeadersResourcePath = "http://localhost:" + port + BlockServer.BLOCK_HEADERS_RESOURCE_PATH;
    protected BlockServer server;

    @Before
    public void setup() {
        try {
            store = BlockStoreFactory.getBlockStore();
            BlockStoreFactory.fillRandomTree(store);
            restClient = new RestClient<>();
            server = new BlockServer(store, port);
        } catch (BlockStoreException e) {
            fail();
        }

    }

    @After
    public void tearDown() {
        try {
            store.destroy();
        } catch (BlockStoreException e) {
            throw new IllegalStateException("Db could not be deleted!");
        }
        if (!IO.deleteDefaultBlockStoreFile())
            throw new IllegalStateException("Db could not be deleted!");
    }

    @Test
    public void testStart() throws Exception {
        server.start();
        assertNotNull(restClient.get(URI.create(blocksResourcePath), Block[].class));
        assertNotNull(restClient.get(URI.create(blockHeadersResourcePath), Block[].class));
        server.stop();
    }
}