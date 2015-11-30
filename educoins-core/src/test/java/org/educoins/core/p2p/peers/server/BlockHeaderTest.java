package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.IBlockIterator;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the {@link BlockController#getBlockHeaders()}.
 * Created by typus on 11/12/15.
 */
public class BlockHeaderTest extends BlockServerTest {

    @Test
    public void Test() throws Exception {
        List<Block> expected = new ArrayList<>();
        IBlockIterator iterator = store.iterator();
        while (iterator.hasNext()) {
            expected.add(iterator.next().getHeader());
        }
        Block[] headers = restClient.get(URI.create(blockHeadersResourcePath), Block[].class);

        for (int i = 0; i < headers.length; i++) {
            assertEquals(headers[i], expected.get(i));
        }
    }
}