package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.utils.RestClient;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the  {@link BlockServlet}
 * Created by typus on 11/12/15.
 */
public class BlockServletTest extends BlockServerTest {
    @Test
    public void TestBlocks() throws Exception {
        server.start();
        List<Block> expected = new ArrayList<>();
        IBlockIterator iterator = store.iterator();
        while (iterator.hasNext()) {
            expected.add(iterator.next());
        }
        Block[] headers = restClient.get(URI.create(blocksResourcePath), Block[].class);

        for (int i = 0; i < headers.length; i++) {
            assertEquals(headers[i], expected.get(i));
        }
        server.stop();
    }

    @Test
    public void TestSpecificBlock() throws Exception {
        server.start();
        Block expected = store.iterator().next();
        Block block = new RestClient<Block>()
                .get(URI.create(blocksResourcePath + expected.hash().toString()), Block.class);

        assertEquals(expected, block);
        server.stop();
    }
}
