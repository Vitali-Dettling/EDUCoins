package org.educoins.core.store;

import org.educoins.core.Block;
import org.educoins.core.Transaction;
import org.educoins.core.testutils.BlockStoreFactory;
import org.educoins.core.utils.IO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Default test for {@link LevelDbBlockStore}
 * Created by typus on 10/19/15.
 */
public class LevelDbBlockStoreTest {
    private IBlockStore store;

    @Before
    public void setup() {
        try {
            store = BlockStoreFactory.getBlockStore();
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
    public void testPut() throws Exception {
        Block b1 = BlockStoreFactory.getRandomBlock();
        store.put(b1);

        BlockStoreFactory.fillRandom(store);

        Block actual = store.get(b1.hash());
        byte[] expected = Block.hash(b1);
        byte[] actualBytes = Block.hash(actual);

        assertEquals(expected.length, actualBytes.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actualBytes[i]);
        }
    }

    @Test
    public void testIterator() throws BlockNotFoundException {
        BlockStoreFactory.fillRandomTree(store);

        int itemCount = 1;
        IBlockIterator iterator = store.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            itemCount++;
        }

        assertTrue(itemCount == 23);
    }

    @Test
    public void testPutWithTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setVersion(100);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Block b1 = BlockStoreFactory.getRandomBlock();
        b1.addTransactions(transactions);

        store.put(b1);

        Block b2 = store.get(Block.hash(b1));
        assert b2 != null;
        assertEquals(1, b2.getTransactionsCount());

        Transaction persisted = b2.getTransactions().get(0);

        assertEquals(transaction.getVersion(), persisted.getVersion());
    }

    @Test
    public void testGetLatest() {
        Block latest = store.getLatest();
        assertNull(latest);

        BlockStoreFactory.fillRandom(store);

        Block b1 = BlockStoreFactory.getRandomBlock();
        store.put(b1);

        Block actual = store.getLatest();

        byte[] expected = Block.hash(b1);
        byte[] actualBytes = Block.hash(actual);

        assertEquals(expected.length, actualBytes.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actualBytes[i]);
        }
    }
}