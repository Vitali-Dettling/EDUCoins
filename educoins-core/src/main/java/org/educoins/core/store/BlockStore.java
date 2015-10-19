package org.educoins.core.store;

import org.educoins.core.Block;

/**
 * The interface representing all logic necessary to store {@link Block}s.
 * Created by typus on 10/18/15.
 */
public interface BlockStore {
    void put(Block block) throws BlockStoreException;

    Block get(String hash) throws BlockStoreException;

    void destroy() throws BlockStoreException;
}