package org.educoins.core.p2p.discovery;

import com.google.common.collect.Sets;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Created by typus on 1/14/16.
 */
public class PeerDiscovery implements DiscoveryStrategy {
    private static Logger logger = LoggerFactory.getLogger(PeerDiscovery.class);
    private Collection<RemoteProxy> peers;

    public PeerDiscovery(Collection<RemoteProxy> peers) {
        this.peers = peers;
    }

    @Override
    public void hello() throws DiscoveryException {
        final int[] cnt = {0};
        peers.forEach(peer -> {
            try {
                peer.hello();
            } catch (IOException e) {
                cnt[0]++;
            }
        });
        if (cnt[0] >= peers.size())
            throw new DiscoveryException("Could not hello even a single node!");
    }

    @Override
    public @NotNull Collection<RemoteProxy> getPeers() throws DiscoveryException {
        Set<RemoteProxy> peers = Sets.newConcurrentHashSet();
        this.peers.parallelStream().forEach(proxy -> {
            try {
                if (this.peers.size() < IProxyPeerGroup.MAX_PROXIES_SIZE)
                    proxy.hello().forEach(peers::add);
            } catch (IOException e) {
            }
        });
        if (peers.size() == 0)
            throw new DiscoveryException("Could not even retrieve a single node!");

        logger.info("Found {} new peer(s)!", peers.size() - this.peers.size());
        return peers;
    }


}
