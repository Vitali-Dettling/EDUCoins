package educoins.core.store; 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.Gate;
import org.educoins.core.Gateway;
import org.educoins.core.Transaction;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import educoins.core.utils.Generator;
import educoins.core.utils.MockedWallet;
import educoins.core.utils.PathHandler;

/**
 * Default test for {@link LevelDbBlockStore}
 * Created by typus on 10/19/15.
 */
public class LevelDbBlockStoreTest {

	private File DIRECTORY = PathHandler.DIRECTORY_DB;
			
    private IBlockStore store;

    @Before
    public void setup() {
        store = new LevelDbBlockStore(DIRECTORY);

        Block block = new Block();
        block.setBits(Sha256Hash.wrap(ByteArray.convertFromString("0101010101010111101")));
        block.setHashMerkleRoot(Sha256Hash.wrap(ByteArray.convertFromString("01234125125")));
        block.setNonce(12314);
        block.setVersion(2);
    }

    @After
    public void tearDown() {
        store.destroy();
        boolean delete = deleteDir(DIRECTORY);

        if (!delete)
            throw new IllegalStateException("Db could not be deleted!");
    }

    @Test
    public void testPut() throws Exception {
    	Block b1 = getRandomBlock();
        store.put(b1);

        fillRandom();

        Block actual = store.get(b1);
        byte[] expected = Block.hash(b1).getBytes();
        byte[] actualBytes = Block.hash(actual).getBytes();

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

        Block b2 = store.get(b1);
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

         byte[] expected = Block.hash(b1).getBytes();
         byte[] actualBytes = Block.hash(actual).getBytes();

         assertEquals(expected.length, actualBytes.length);

         for (int i = 0; i < expected.length; i++) {
             assertEquals(expected[i], actualBytes[i]);
         }
    }
    
    @Test
    public void testStoreGateway(){
    	
    	String randomNumber = Generator.getSecureRandomString256HEX();
    	final String publicKey = MockedWallet.getPublicKey();
    	final String signature = MockedWallet.getSignature(publicKey, randomNumber);
    	
    	Gate gate = new Gate(null, publicKey);
    	Gateway gateway = new Gateway();
    	List<Gateway> gateways = new ArrayList<Gateway>();
    	Block block = new Block();
    	
    	gate.setSignature(signature);
		gateway.addGate(gate);
		gateways.add(gateway);
		block.addAllGateways(gateways);
		
		this.store.put(block);
		Block storedBlock = this.store.get(block);
		
		assertTrue(!storedBlock.getGateways().isEmpty());
		
		String originalBlock = block.toString();
		String storedDBBlock = storedBlock.toString();
		
		assertEquals(originalBlock, storedDBBlock);	
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
        toReturn.setBits(Sha256Hash.wrap(ByteArray.convertFromInt((int) (Math.random() * Integer.MAX_VALUE))));
        String trueRandom =  Generator.getSecureRandomString256HEX();
        toReturn.setHashMerkleRoot(Sha256Hash.wrap(trueRandom));
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