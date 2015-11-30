package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.nodes.LocalPeer;
import org.educoins.core.p2p.nodes.Peer;
import org.educoins.core.store.IBlockStore;

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

    @Override
    public Collection<Peer> getPeers() {
        /**
         * So far, it doesn't make sense to add more than one local peer.
         */
        Collection<Peer> remoteNodes = new ArrayList<>();
        remoteNodes.add(new LocalPeer(blockStore));
        return remoteNodes;
    }
}
