package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.utils.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the  {@link BlockController#getBlocks()} and {@link BlockController#getBlockHeaders()}.
 * Created by typus on 11/12/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PeerServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8090")
public class BlockControllerTest {

    protected RestClient<Block[]> restClient = new RestClient<>();
    protected int port = 8090;
    protected String blocksResourcePath = "http://localhost:" + port + PeerServer.BLOCKS_RESOURCE_PATH;
    @Autowired
    private IBlockStore store;

    @Test
    public void TestBlocks() throws Exception {
        List<Block> expected = new ArrayList<>();
        IBlockIterator iterator = store.iterator();
        while (iterator.hasNext()) {
            expected.add(iterator.next());
        }
        URI uri = URI.create(blocksResourcePath + "from/" + iterator.get().hash());
        Block[] blocks = restClient.get(uri, Block[].class);

        for (int i = 0; i < blocks.length; i++) {
            assertEquals(blocks[i], expected.get(i));
        }
    }

    @Test
    public void TestSpecificBlock() throws Exception {
        store.put(BlockStoreFactory.getRandomBlock(store.getLatest()));
        Block expected = store.iterator().next();
        Block block = new RestClient<Block>()
                .get(URI.create(blocksResourcePath + expected.hash().toString()), Block.class);

        assertEquals(expected, block);
    }
}
