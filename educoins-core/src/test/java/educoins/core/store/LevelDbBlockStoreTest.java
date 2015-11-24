package educoins.core.store;

import educoins.core.utils.BlockStoreFactory;
import org.educoins.core.Block;
import org.educoins.core.Transaction;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.IO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Default test for {@link org.educoins.core.store.LevelDbBlockStore}
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
        if (!IO.deleteDefaultBlockStoreFile())
            throw new IllegalStateException("Db could not be deleted!");
    }

    @Test
    public void testPut() throws Exception {
        Block b1 = BlockStoreFactory.getRandomBlock();
        this.store.put(b1);
        
        int filled = 23;
        BlockStoreFactory.fillRandom(this.store, filled);

        Block actual = this.store.get(b1.hash());
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
        IBlockIterator iterator = this.store.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            itemCount++;
        }

        assertTrue(itemCount == filled);
    }

    @Test
    public void testPutWithTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setVersion(100);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Block b1 = BlockStoreFactory.getRandomBlock();
        b1.addTransactions(transactions);

        this.store.put(b1);

        Block b2 = this.store.get(b1.hash());
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