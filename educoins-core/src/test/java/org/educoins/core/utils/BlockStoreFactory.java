package org.educoins.core.utils;

import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.Wallet;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.core.transaction.CoinbaseTransaction;
import org.educoins.core.transaction.Input;
import org.educoins.core.transaction.Output;
import org.educoins.core.transaction.RegularTransaction;
import org.educoins.core.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

/**
 * A factory for easier testing concerning the {@link IBlockStore}.
 * Created by typus on 11/12/15.
 */
public class BlockStoreFactory {
    public static IBlockStore getBlockStore() throws BlockStoreException {
        return new LevelDbBlockStore(IO.getDefaultBlockStoreFile());
    }

    public static IBlockStore getRandomlyFilledBlockStore() throws BlockStoreException {
        return new LevelDbBlockStore(IO.getDefaultBlockStoreFile());
    }

    public static void fillRandom(IBlockStore store, int filled) {
        for (int i = 0; i < filled; i++) {
            store.put(getRandomBlock());
        }
    }

    public static void fillRandomTree(IBlockStore store, int filled) {
        Block previous = store.getLatest();
        for (int i = 0; i < filled; i++) {
            previous = getRandomBlockChain(previous);
            store.put(previous);
        }
    }
    
    public static Block getRandomBlockChain(Block block) {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setBits(Sha256Hash.wrap(ByteArray.convertFromInt((int) (Math.random() * Integer.MAX_VALUE))));
        String random =  Generator.getSecureRandomString256HEX();
        toReturn.setHashMerkleRoot(Sha256Hash.wrap(random));
        toReturn.setHashPrevBlock(block.hash());
        return toReturn;
    }

    public static Block getRandomBlock() {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setBits(Sha256Hash.wrap(ByteArray.convertFromInt((int) (Math.random() * Integer.MAX_VALUE))));
        String random =  Generator.getSecureRandomString256HEX();
        toReturn.setHashMerkleRoot(Sha256Hash.wrap(random));
        return toReturn;
    }

    public static Transaction generateTransaction(int number) {
    	Wallet mockedWallet = MockedWallet.getMockedWallet();
    	List<Output> copyPreviousOutputs = TxFactory.getRandomPreviousOutputs();
        Transaction t = new RegularTransaction(copyPreviousOutputs, 2, 3, Wallet.getPublicKey());
        for (int i = 0; i < 2 * number; i++) {
            Input input = new Input(5 * i * number, "",  mockedWallet.getPublicKey());
            t.addInput(input);
        }
        for (int i = 0; i < 4 * number; i++) {
            t.addOutput(new Output(5 * i * number, mockedWallet.getPublicKey()));
        }
        t.setApprovals(null);
        return t;
    }
    
    public static Transaction generateTransactionWithSameUnlockingScript(int number) {
    	Transaction t = new CoinbaseTransaction(2, "abc");
        for (int i = 0; i < 2 * number; i++) {
            Input input = new Input(5 * i * number, "",  "ABC");
            t.addInput(input);
        }
        for (int i = 0; i < 4 * number; i++) {
            t.addOutput(new Output(5 * i * number, "ABC"));
        }
        t.setApprovals(null);
        return t;
    }
    
    
	public static Input generateRandomInput(String hashPrevOutput){
		
		int amount = (int) (Math.random() * Integer.MAX_VALUE);
		Input input = new Input(amount, hashPrevOutput, "12345");
		return input;
	}
}
