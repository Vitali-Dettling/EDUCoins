package org.educoins.core.p2p;

import com.sun.istack.internal.NotNull;
import org.educoins.core.Block;
import org.educoins.core.IBlockListener;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.discovery.DiscoveryStrategy;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.Threading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The P2p specific implementation of an {@link IBlockReceiver}.
 * Created by typus on 10/27/15.
 */
public class P2pBlockReceiver implements IBlockReceiver {

    private final IBlockStore blockStore;
    private final Set<IBlockListener> blockListeners = new HashSet<>();

    private DiscoveryStrategy discovery;

    public P2pBlockReceiver(@NotNull IBlockStore blockStore, @NotNull DiscoveryStrategy discovery) {
        this.blockStore = blockStore;
        this.discovery = discovery;
    }

    @Override
    public void addBlockListener(IBlockListener blockListener) {
        this.blockListeners.add(blockListener);
    }

    @Override
    public void removeBlockListener(IBlockListener blockListener) {
        this.blockListeners.remove(blockListener);
    }

    @Override
    public void receiveBlocks() {
        Collection<Block> blockList = new ArrayList<>();

        try {

            //only FullPeers are allowed to change data also they are the only Peers to provide block data.
            for (Peer peer : discovery.getFullPeers()) {
                mergeBlocks(peer.getBlocks(), blockList);
            }

            blockList.forEach(block -> {
                Threading.run(() -> blockStore.put(block));
                blockListeners.forEach(iBlockListener -> iBlockListener.blockReceived(block));
            });

        } catch (DiscoveryException e) {
            //TODO: catch or throw?
            e.printStackTrace();
        }

    }


    private void mergeBlocks(Collection<Block> newBlocks, Collection<Block> globalBlocks) {
        //TODO: replace this by meaningful branching/merging logic.
        globalBlocks.addAll(newBlocks);
    }
}
