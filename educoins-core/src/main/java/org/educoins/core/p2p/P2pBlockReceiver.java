package org.educoins.core.p2p;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.discovery.DiscoveryStrategy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.Threading;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * The P2p specific implementation of an {@link IBlockReceiver}.
 * Created by typus on 10/27/15.
 */
public class P2pBlockReceiver implements IBlockReceiver {

    private final Logger logger = LoggerFactory.getLogger(P2pBlockReceiver.class);

    private final IBlockStore blockStore;
    private final Set<IBlockListener> blockListeners = new HashSet<>();

    private DiscoveryStrategy discovery;

    public P2pBlockReceiver(@NotNull IBlockStore blockStore, @NotNull DiscoveryStrategy discovery) {
        this.blockStore = blockStore;
        this.discovery = discovery;
    }

    @Override
    public void addBlockListener(@NotNull IBlockListener blockListener) {
        logger.debug("adding bocklistener: " + blockListener.getClass().getName());
        this.blockListeners.add(blockListener);
    }

    @Override
    public void removeBlockListener(@NotNull IBlockListener blockListener) {
        logger.debug("removing bocklistener: " + blockListener.getClass().getName());
        this.blockListeners.remove(blockListener);
    }

    @Override
    public void receiveBlocks() {
        logger.info("Fetching blocks.");
        Collection<Block> blockList = new ArrayList<>();

        try {

            //only FullPeers are allowed to change data also they are the only Peers to provide block data.
            for (RemoteProxy peer : discovery.getPeers()) {
                try {

                    mergeBlocks(peer.getBlocks(), blockList);

                } catch (IOException e) {
                    logger.warn("One of the peers retrieved via discovery did not respond properly", e);
                }
            }

            blockList.forEach(block -> {
                Threading.run(() -> blockStore.put(block));
                blockListeners.forEach(iBlockListener -> iBlockListener.blockReceived(block));
            });

        } catch (DiscoveryException e) {
            logger.error("Peer discovery failed! We are now in an invalid state!", e);
        }

    }


    private void mergeBlocks(@NotNull Collection<Block> newBlocks, @NotNull Collection<Block> globalBlocks) {
        //TODO: replace this by meaningful branching/merging logic.
        globalBlocks.addAll(newBlocks);
    }
}
