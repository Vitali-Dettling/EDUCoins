package org.educoins.demo.client;

import org.educoins.core.*;
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
        File defaultBlockStoreFile = IO.getDefaultBlockStoreFile();
        blockStores.add(defaultBlockStoreFile);
        return new LevelDbBlockStore(defaultBlockStoreFile);
    }

    public static void removeAllBlockStores() throws IOException {
        for (File file : blockStores) {
            IO.deleteDirectory(file.getAbsolutePath());
        }
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
        Block toReturn = getRandomBlock();
        toReturn.setHashPrevBlock(block.hash());
        return toReturn;
    }

    public static Block getRandomBlock() {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        for (int i = 0; i < Math.random() * 10; ++i)
            toReturn.addTransaction(generateTransaction(2));
        return toReturn;
    }

    public static Transaction generateTransaction(int number) {
        Transaction t = new Transaction();
        for (int i = 0; i < 2 * number; i++) {
            Input input = new Input(5 * i * number, "", i);
            input.setUnlockingScript(Input.EInputUnlockingScript.PUBLIC_KEY, "12345");
            t.addInput(input);
        }
        for (int i = 0; i < 4 * number; i++) {
            t.addOutput(new Output(5 * i * number, "", "123"));
        }
        t.setApprovals(null);
        return t;
    }
}
