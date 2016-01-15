package org.educoins.core.store;

import org.educoins.core.Block;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface representing all logic necessary to store {@link Block}s.
 * Created by typus on 10/18/15.
 */
public interface IBlockStore {
    /**
     * Save a {@link Block} in the IBlockStore. This put-action is also used to update a block.
     *
     * @param block the {@link Block} to store.
     */
    void put(@NotNull Block block);

    /**
     * Retrieves a {@link Block} identified by the given hash.
     *
     * @param hash the identification for the {@link Block}
     * @return the Block if found.
     * @throws BlockNotFoundException if the {@link Block} could not be found.
     */
    @NotNull Block get(Sha256Hash hash) throws BlockNotFoundException;

    /**
     * Retrieves the latest {@link Block} stored in the {@link IBlockStore}. Or Null if there is none.
     *
     * @return the latest Block.
     */
    @Nullable Block getLatest();

    /**
     * Closes the Store.
     *
     * @throws BlockStoreException
     */
    void destroy() throws BlockStoreException;

    /**
     * Returns the genesis block. This is most likely the first block inserted.
     *
     * @return the GenesisBlock.
     * @throws BlockNotFoundException if there is no genesis block in the store right now.
     */
    @NotNull Block getGenesisBlock() throws BlockNotFoundException;

    /**
     * Checks whether the given {@link Block} is already stored in the store.
     *
     * @param block the {@link Block} to look for.
     * @return true if found, false if not.
     */
    boolean contains(Block block);

    /**
     * Initializes an {@link IBlockIterator} pointing to the latest {@link Block} stored in the {@link IBlockStore}.
     *
     * @return the initialized {@link IBlockIterator}.
     */
    IBlockIterator iterator();


}