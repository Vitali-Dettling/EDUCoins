package org.educoins.core.store;

import junit.framework.TestCase;
import org.educoins.core.Block;
import org.fusesource.leveldbjni.JniDBFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by typus on 10/19/15.
 */
public class LevelDbBlockStoreTest extends TestCase {

    private BlockStore store;
    private Block block;

    @Before
    public void setup() {
        try {
            File file = File.createTempFile("blockStore", null);
            file.mkdir();
            store = new LevelDbBlockStore(file, JniDBFactory.factory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        block = new Block();
        block.setBits("blablabla");
        block.setHashMerkleRoot("bla");
        block.setNonce(12314);
        block.setVersion(2);
    }

    @Test
    public void testPut() throws Exception {
        setup();

        store.put(block);
        assertEquals(block, store.get(block.getHashMerkleRoot()));
    }

    @Test
    public void testGet() throws Exception {

    }
}