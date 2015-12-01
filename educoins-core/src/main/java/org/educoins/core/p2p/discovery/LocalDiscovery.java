package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.remote.LocalProxy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.educoins.core.store.IBlockStore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The LocalDiscovery is mostly used for demo purposes since it does only provides blocks from the local machine.
 * There is no networking needed.
 * Created by typus on 10/27/15.
 */
public class LocalDiscovery implements DiscoveryStrategy {

    private final IBlockStore blockStore;

    public LocalDiscovery(IBlockStore blockStore) {
        this.blockStore = blockStore;
    }


    public Collection<RemoteProxy> getPeers() {
        /**
         * So far, it doesn't make sense to add more than one local peer.
         */
        Collection<RemoteProxy> remoteNodes = new ArrayList<>();
        remoteNodes.add(new LocalProxy(blockStore));
        return remoteNodes;
    }


    @Override
    public @NotNull Collection<RemoteProxy> getReferencePeers() throws DiscoveryException {
        return getPeers();
    }

    @Override
    public @NotNull Collection<RemoteProxy> getFullBlockchainPeers() throws DiscoveryException {
        return getPeers();
    }

    @Override
    public @NotNull Collection<RemoteProxy> getSoloMinerPeers() throws DiscoveryException {
        return getPeers();
    }
}
