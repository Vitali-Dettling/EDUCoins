package org.educoins.core.p2p.peers.remote;

import org.educoins.core.Block;
import org.educoins.core.store.*;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simulating a Remote Peer.
 * Created by typus on 10/27/15.
 */
public class LocalProxy extends RemoteProxy {

    private final Logger logger = LoggerFactory.getLogger(LocalProxy.class);
    private final IBlockStore blockStore;

    public LocalProxy(IBlockStore blockStore) {
        this.blockStore = blockStore;
    }

    @Override
    @NotNull
    public Collection<Block> getHeaders() throws IOException {
        return getBlocks().stream().map(Block::getHeader).collect(Collectors.toList());
    }

    @Override
    @NotNull
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

    @Override
    @Nullable
    public Block getBlock(Sha256Hash hash) throws IOException {
        try {
            return blockStore.get(hash);
        } catch (BlockNotFoundException ex) {
            logger.debug("Block not found!", ex);
            return null;
        }
    }

    @Override
    public Collection<RemoteProxy> hello() throws IOException {
        return Arrays.asList(new RemoteProxy[]{this});
    }

}
