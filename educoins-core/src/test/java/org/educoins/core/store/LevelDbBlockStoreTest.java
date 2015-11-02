package org.educoins.core.store;

import org.educoins.core.Block;
import org.educoins.core.Transaction;
import org.fusesource.leveldbjni.JniDBFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Default test for {@link LevelDbBlockStore}
 * Created by typus on 10/19/15.
 */
public class LevelDbBlockStoreTest {

    public static final File DIRECTORY = new File("/tmp/blockstore");
    private IBlockStore store;
    private Block block;

    @Before
    public void setup() {
        store = new LevelDbBlockStore(DIRECTORY, JniDBFactory.factory);

        block = new Block();
        block.setBits("0101010101010111101");
        block.setHashMerkleRoot("01234125125");
        block.setNonce(12314);
        block.setVersion(2);
    }

    @After
    public void tearDown() {
        store.destroy();
        boolean delete = deleteDir(DIRECTORY);
        System.out.println("Deleted? " + delete);
    }

    @Test
    public void testPut() throws Exception {
        Block b1 = getRandomBlock();
        store.put(b1);

        fillRandom();

        Block actual = store.get(Block.hash(b1));
        byte[] expected = Block.hash(b1);
        byte[] actualBytes = Block.hash(actual);

        assertEquals(expected.length, actualBytes.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actualBytes[i]);
        }
    }

    @Test
    public void testPutWithTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setVersion(100);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Block b1 = getRandomBlock();
        b1.addTransactions(transactions);

        store.put(b1);

        Block b2 = store.get(Block.hash(b1));
        assertEquals(1, b2.getTransactionsCount());

        Transaction persisted = b2.getTransactions().get(0);

        assertEquals(transaction.getVersion(), persisted.getVersion());
    }

    @Test
    public void testGetLatest() {
        Block latest = store.getLatest();
        assertNull(latest);

        fillRandom();

        Block b1 = getRandomBlock();
        store.put(b1);

        Block actual = store.getLatest();

        byte[] expected = Block.hash(b1);
        byte[] actualBytes = Block.hash(actual);

        assertEquals(expected.length, actualBytes.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actualBytes[i]);
        }
    }

    private void fillRandom() {
        for (int i = 0; i < 23; i++) {
            store.put(getRandomBlock());
        }
    }

    private Block getRandomBlock() {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setBits((int) (Math.random() * Integer.MAX_VALUE) + "");
        toReturn.setHashMerkleRoot((int) (Math.random() * Integer.MAX_VALUE) + "");
        return toReturn;
    }

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}