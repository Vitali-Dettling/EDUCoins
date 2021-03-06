package org.educoins.core.store;

import org.jetbrains.annotations.NotNull;
import org.educoins.core.Block;

/**
 * An {@link java.util.Iterator} exposed by {@link IBlockStore#iterator()}, to iterate over all {@link Block}s in certain chain.
 * Created by typus on 11/5/15.
 */
public interface IBlockIterator {
    boolean hasNext();

    @NotNull
    Block next() throws BlockNotFoundException;
}
