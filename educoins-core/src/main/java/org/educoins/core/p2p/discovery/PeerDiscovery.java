package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by typus on 1/14/16.
 */
public class PeerDiscovery implements DiscoveryStrategy {
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
            throw new DiscoveryException("Could not hello even a single Node!");
    }

    @Override
    public @NotNull Collection<RemoteProxy> getPeers() throws DiscoveryException {
        List<RemoteProxy> peers = new CopyOnWriteArrayList<>();
        this.peers.parallelStream().forEach(proxy -> {
            try {
                if (this.peers.size() < IProxyPeerGroup.MAX_PROXIES_SIZE)
                    proxy.hello().forEach(peers::add);
            } catch (IOException e) {
            }
        });
        return peers;
    }


}
