package org.educoins.core.p2p.peers;

import org.educoins.core.Block;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simulating a Remote Peer.
 * Created by typus on 10/27/15.
 */
public class LocalPeer extends Peer {

    private final IBlockStore blockStore;

    public LocalPeer(IBlockStore blockStore) {
        super(null);
        this.blockStore = blockStore;
    }

    @Override
    public Collection<Block> getBlocks() {
        Collection<Block> allBlocks = new ArrayList<>();

        IBlockIterator iterator = blockStore.iterator();

        while (iterator.hasNext()) {
            try {
                allBlocks.add(iterator.next());
            } catch (BlockNotFoundException e) {
                //TODO: errorhandling
            }
        }

        return allBlocks;
    }
}
