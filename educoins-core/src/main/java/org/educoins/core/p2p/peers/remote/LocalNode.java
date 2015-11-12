package org.educoins.core.p2p.peers.remote;

import org.educoins.core.Block;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Simulating a Remote Peer.
 * Created by typus on 10/27/15.
 */
public class LocalNode extends RemoteNode {

    private final Logger logger = LoggerFactory.getLogger(LocalNode.class);
    private final IBlockStore blockStore;

    public LocalNode(IBlockStore blockStore) {
        this.blockStore = blockStore;
    }

    @Override
    public Collection<Block> getHeaders() throws IOException {
        return getBlocks().stream().map(Block::getHeader).collect(Collectors.toList());
    }

    @Override
    public Collection<Block> getBlocks() {
        Collection<Block> allBlocks = new ArrayList<>();

        IBlockIterator iterator = blockStore.iterator();

        while (iterator.hasNext()) {
            try {
                allBlocks.add(iterator.next());
            } catch (BlockNotFoundException e) {
                logger.error("Error while storing blocks!", e);
            }
        }

        return allBlocks;
    }
}
