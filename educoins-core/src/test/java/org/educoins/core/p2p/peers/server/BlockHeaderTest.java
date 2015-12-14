package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
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
 * Tests the {@link BlockController#getBlockHeaders()}.
 * Created by typus on 11/12/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PeerServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8091")
public class BlockHeaderTest {
    @Autowired
    protected IBlockStore store;

    protected RestClient<Block[]> restClient = new RestClient<>();
    protected int port = 8091;
    protected String blockHeadersResourcePath = "http://localhost:" + port + PeerServer.BLOCK_HEADERS_RESOURCE_PATH;

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