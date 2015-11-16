package org.educoins.core.p2p.nodes;

import org.educoins.core.Block;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockStore;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simulating a Remote Peer.
 * Created by typus on 10/27/15.
 */
public class LocalPeer implements Peer {

    private final IBlockStore blockStore;

    public LocalPeer(IBlockStore blockStore) {
        this.blockStore = blockStore;
    }

    @Override
    public Collection<Block> getBlocks() {
        Collection<Block> allBlocks = new ArrayList<>();

        Block anchorBlock = blockStore.getLatest();
        while (anchorBlock != null) {
            allBlocks.add(anchorBlock);
            try {
                anchorBlock = blockStore.get(anchorBlock);
            } catch (BlockNotFoundException ex) {
                anchorBlock = null;
            }
        }

        return allBlocks;
    }
}
