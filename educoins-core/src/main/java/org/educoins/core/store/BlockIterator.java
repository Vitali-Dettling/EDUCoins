package org.educoins.core.store;

import org.educoins.core.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * An {@link IBlockIterator} exposed by {@link IBlockStore#iterator()}, to iterate over all {@link Block}s
 * in a certain chain stored in the {@link LevelDbBlockStore}.
 * Created by typus on 11/5/15.
 */
public class BlockIterator implements IBlockIterator {

    private final IBlockStore blockStore;
    private final byte[] genesisHash;
    private Block currentElement;

    public BlockIterator(IBlockStore blockStore, byte[] genesisHash) {
        this.blockStore = blockStore;
        this.genesisHash = genesisHash;
        currentElement = blockStore.getLatest();

    }

    @Override
    public boolean hasNext() {
        return !Arrays.equals(currentElement.hash(), genesisHash);
    }

    @Override
    @NotNull
    public Block next() throws BlockNotFoundException {
        Block elementToReturn = currentElement;
        currentElement = blockStore.get(currentElement.getHashPrevBlock());
        return elementToReturn;
    }
}
