package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.utils.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PeerNode representation. Necessary for P2P Networking. The concrete
 * implementations are the following: Reference Client->miner,blockchain,wallet
 * Full BlockChain->blockchain Solo Miner->miner,blockchain Created by typus on
 * 10/27/15.
 */
public abstract class Peer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final IProxyPeerGroup proxyPeerGroup;
    protected final BlockChain blockChain;
    protected final Sha256Hash publicKey;

    protected Peer(BlockChain blockChain, IProxyPeerGroup proxyPeerGroup, Sha256Hash publicKey) {
        this.proxyPeerGroup = proxyPeerGroup;
        this.blockChain = blockChain;
        this.publicKey = publicKey;

        this.proxyPeerGroup.addBlockListener(blockChain);
    }

    public void start() throws DiscoveryException {
        this.proxyPeerGroup.discover();
        logger.info("Starting {}", getClass().getName());
    }

    public abstract void stop();

    // region equality
    @Override
    public int hashCode() {
        int result = logger != null ? logger.hashCode() : 0;
        result = 31 * result + (proxyPeerGroup != null ? proxyPeerGroup.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Peer peer = (Peer) o;

        return !(logger != null ? !logger.equals(peer.logger) : peer.logger != null)
                && !(proxyPeerGroup != null ? !proxyPeerGroup.equals(peer.proxyPeerGroup) : peer.proxyPeerGroup != null);

    }
    // endregion

}
