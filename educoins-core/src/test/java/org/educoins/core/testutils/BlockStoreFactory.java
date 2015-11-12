package org.educoins.core.testutils;

import org.educoins.core.Block;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.core.utils.IO;

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

    public static void fillRandom(IBlockStore store) {
        for (int i = 0; i < 23; i++) {
            store.put(getRandomBlock());
        }
    }

    public static void fillRandomTree(IBlockStore store) {
        Block previous = getRandomBlock();
        for (int i = 0; i < 23; i++) {
            previous = getRandomBlock(previous);
            store.put(previous);
        }
    }

    public static Block getRandomBlock(Block block) {
        Block toReturn = new Block();
        toReturn.setHashPrevBlock(block.hash());
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setBits((int) (Math.random() * Integer.MAX_VALUE) + "");
        toReturn.setHashMerkleRoot(((Math.random() * Integer.MAX_VALUE) + "").getBytes());
        return toReturn;
    }

    public static Block getRandomBlock() {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setBits((int) (Math.random() * Integer.MAX_VALUE) + "");
        toReturn.setHashMerkleRoot(((Math.random() * Integer.MAX_VALUE) + "").getBytes());
        return toReturn;
    }
}
