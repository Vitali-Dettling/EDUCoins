package org.educoins.core.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.Transaction;
import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.IO;
import org.educoins.core.utils.IO.EPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Default test for {@link LevelDbBlockStore}
 * Created by typus on 10/19/15.
 */
public class LevelDbBlockStoreTest {
	
    private IBlockStore store;

    @Before
    public void setup() {
        try {
            this.store = BlockStoreFactory.getBlockStore();
        } catch (BlockStoreException e) {
            fail();
        }
    }

    @After
    public void tearDown() {
        try {
            this.store.destroy();
        } catch (BlockStoreException e) {
            throw new IllegalStateException("Db could not be deleted!");
        }
        if (!IO.deleteDefaultFileLocation(EPath.TMP, EPath.EDUCoinsBlockStore))
            throw new IllegalStateException("Db could not be deleted!");
    }

    @Test
    public void testPutGenesisBlockOnly() throws Exception {
        
    	Block b1 = BlockStoreFactory.getRandomBlock();
        this.store.put(b1);
        
        IBlockIterator iterator = this.store.blockIterator();
        assertFalse(iterator.hasNext());

    }
    
    @Test
    public void testPut() throws Exception {
        Block b1 = BlockStoreFactory.getRandomBlock();
        this.store.put(b1);
        
        int filled = 23;
        BlockStoreFactory.fillRandom(this.store, filled);

        Block actual = this.store.get(b1.hash().getBytes());
        byte[] expected = Block.hash(b1).getBytes();
        byte[] actualBytes = Block.hash(actual).getBytes();

        assertEquals(expected.length, actualBytes.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actualBytes[i]);
        }
    }

    @Test
    public void testIterator() throws BlockNotFoundException {
    	
    	int filled = 23;
        BlockStoreFactory.fillRandomTree(this.store, filled);

        int itemCount = 1;
        IBlockIterator iterator = this.store.blockIterator();
        while (iterator.hasNext()) {
            iterator.next();
            itemCount++;
        }

        assertTrue(itemCount == filled);
    }

    @Test
    public void testPutWithTransaction() throws Exception {
        Transaction transaction = BlockStoreFactory.generateTransaction(1);
        transaction.setVersion(100);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Block b1 = BlockStoreFactory.getRandomBlock();
        b1.addTransactions(transactions);

        this.store.put(b1);

        Block b2 = this.store.get(Block.hash(b1).getBytes());
        assert b2 != null;
        assertEquals(1, b2.getTransactionsCount());

        Transaction persisted = b2.getTransactions().get(0);

        assertEquals(transaction.getVersion(), persisted.getVersion());
    }

    @Test
    public void testGetLatest() {
        Block latest = this.store.getLatest();
        assertNull(latest);
        int filled = 23;
        BlockStoreFactory.fillRandom(this.store, filled);

        Block b1 = BlockStoreFactory.getRandomBlock();
        this.store.put(b1);

        Block actual = this.store.getLatest();

        byte[] expected = Block.hash(b1).getBytes();
        byte[] actualBytes = Block.hash(actual).getBytes();

        assertEquals(expected.length, actualBytes.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actualBytes[i]);
        }
    }
}