package org.educoins.core.testutils;

import org.educoins.core.Block;
import org.educoins.core.store.*;
import org.educoins.core.utils.IO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A factory for easier testing concerning the {@link IBlockStore}.
 * Created by typus on 11/12/15.
 */
public class BlockStoreFactory {
    static List<File> blockStores = new ArrayList<>();

    public static IBlockStore getBlockStore() throws BlockStoreException {
        File defaultBlockStoreFile = IO.getRandomizedBlockStoreFile();
        blockStores.add(defaultBlockStoreFile);
        return new LevelDbBlockStore(defaultBlockStoreFile);
    }

    public static void removeAllBlockStores() throws IOException {
        for (File file : blockStores) {
            IO.deleteDirectory(file.getAbsolutePath());
        }
    }

    public static IBlockStore getRandomlyFilledBlockStore() throws BlockStoreException {
        return new LevelDbBlockStore(IO.getRandomizedBlockStoreFile());
    }

    public static void fillRandom(IBlockStore store) {
        for (int i = 0; i < 23; i++) {
            store.put(getRandomBlock());
        }
    }

    public static void fillRandomTree(IBlockStore store) {
        Block previous = new Block();
        for (int i = 0; i < 3; i++) {
            previous = getRandomBlock(previous);
            store.put(previous);
        }
    }

    public static Block getRandomBlock(Block block) {
        Block toReturn = getRandomBlock();
        toReturn.setHashPrevBlock(block.hash());
        return toReturn;
    }

    public static Block getRandomBlock() {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        return toReturn;
    }

}
