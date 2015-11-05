package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.core.utils.IO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Created by typus on 11/5/15.
 */
public class BlockServerTest {

    private IBlockStore store;
    private String DIRECTORY;

    @Before
    public void setup() {
        try {
            DIRECTORY = "/tmp/blocks";
            store = new LevelDbBlockStore(new File(DIRECTORY));
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
        boolean delete = false;
        try {
            IO.deleteDirectory(DIRECTORY);
        } catch (IOException e) {
            throw new IllegalStateException("Db could not be deleted!");
        }
    }

    @Test
    public void testStart() throws Exception {
        fillRandomTree(store);
        BlockServer server = new BlockServer(store, 8090);
        server.start();
        server.join();
    }

    private void fillRandomTree(IBlockStore store) {
        Block previous = getRandomBlock(null);
        for (int i = 0; i < 23; i++) {
            previous = getRandomBlock(previous);
            store.put(previous);
        }
    }

    private Block getRandomBlock(Block block) {
        Block toReturn = new Block();
        if (block != null)
            toReturn.setHashPrevBlock(block.hash());
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setBits((int) (Math.random() * Integer.MAX_VALUE) + "");
        toReturn.setHashMerkleRoot(((Math.random() * Integer.MAX_VALUE) + "").getBytes());
        return toReturn;
    }
}