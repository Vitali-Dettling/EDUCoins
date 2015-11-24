package org.educoins.core.p2p.nodes;

import java.util.ArrayList;
import java.util.Collection;

import org.educoins.core.Block;
import org.educoins.core.store.IBlockStore;

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
            anchorBlock = blockStore.get(anchorBlock.hash().getBytes());
        }

        return allBlocks;
    }
}
