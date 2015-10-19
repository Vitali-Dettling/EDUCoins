package org.educoins.core.store;

import junit.framework.TestCase;
import org.educoins.core.Block;
import org.fusesource.leveldbjni.JniDBFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by typus on 10/19/15.
 */
public class LevelDbBlockStoreTest extends TestCase {

    private BlockStore store;
    private Block block;

    @Before
    public void setup() {
        File directory = new File("/tmp/blockstore");
        if (directory.mkdir() || directory.exists() && directory.isDirectory())
            store = new LevelDbBlockStore(directory, JniDBFactory.factory);

        block = new Block();
        block.setBits("0101010101010111101");
        block.setHashMerkleRoot("01234125125");
        block.setNonce(12314);
        block.setVersion(2);
    }

    @After
    public void tearDown() {
        store.destroy();
    }

    @Test
    public void testPut() throws Exception {
        store.put(block);
        Block actual = store.get(block.getHashMerkleRoot());
        byte[] expected = Block.hash(block);
        byte[] actualBytes = Block.hash(actual);

        assertEquals(expected.length, actualBytes.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actualBytes[i]);
        }
    }
}