package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.utils.Sha256Hash;

/**
 * The {@link Peer}-type representing a Peer with full capabilities.
 * Created by typus on 11/3/15.
 */
public class FullBlockChainPeer extends Peer {

    protected FullBlockChainPeer(BlockChain blockChain, IProxyPeerGroup proxyPeerGroup, Sha256Hash publicKey) {
        super(blockChain, proxyPeerGroup, publicKey);
    }

    @Override
    public void start() throws DiscoveryException {
        super.start();
    }

    @Override
    public void stop() {

    }
}
