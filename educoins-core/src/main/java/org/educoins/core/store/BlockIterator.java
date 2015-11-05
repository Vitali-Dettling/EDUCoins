package org.educoins.core.store;

import org.educoins.core.Block;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link IBlockIterator} exposed by {@link IBlockStore#iterator()}, to iterate over all {@link Block}s
 * in a certain chain stored in the {@link LevelDbBlockStore}.
 * Created by typus on 11/5/15.
 */
public class BlockIterator implements IBlockIterator {

    private Block currentElement;
    private IBlockStore blockStore;

    public BlockIterator(IBlockStore blockStore) {
        this.blockStore = blockStore;
        currentElement = blockStore.getLatest();
    }

    @Override
    public boolean hasNext() {
        return currentElement.getHashPrevBlock() != null;
    }

    @Override
    @NotNull
    public Block next() throws BlockNotFoundException {
        Block elementToReturn = currentElement;
        currentElement = blockStore.get(currentElement.getHashPrevBlock().getBytes());
        return elementToReturn;
    }
}
