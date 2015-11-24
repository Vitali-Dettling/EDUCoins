package org.educoins.core.store;

import java.util.Arrays;

import org.educoins.core.Block;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link IBlockIterator} exposed by {@link IBlockStore#iterator()}, to iterate over all {@link Block}s
 * in a certain chain stored in the {@link LevelDbBlockStore}.
 * Created by typus on 11/5/15.
 */
public class BlockIterator implements IBlockIterator {

    private final IBlockStore blockStore;
    private final byte[] genesisHash;
    private Block currentElement;

    public BlockIterator(@NotNull IBlockStore blockStore, @NotNull byte[] genesisHash) {
        this.blockStore = blockStore;
        this.genesisHash = genesisHash;
        this.currentElement = blockStore.getLatest();

    }

    @Override
    public boolean hasNext() {
        return !Arrays.equals(this.currentElement.hash().getBytes(), genesisHash);
    }

    @Override
    @NotNull
    public Block next	() {
        Block elementToReturn = this.currentElement;
        byte[] currentHash = currentElement.getHashPrevBlock().getBytes();
        currentElement = blockStore.get(currentHash);
        return elementToReturn;
    }
}
