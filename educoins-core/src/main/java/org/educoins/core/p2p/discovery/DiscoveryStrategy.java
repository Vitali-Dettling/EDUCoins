package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.Peer;

import java.util.Collection;

/**
 * The interface providing functionality to retrieve the necessary {@link Peer}s.
 * This interface should represent the Strategy-Pattern.
 * Created by typus on 10/27/15.
 */
public interface DiscoveryStrategy {
    /**
     * Discovers the specific {@link org.educoins.core.p2p.peers.FullPeer}s.
     *
     * @return a {@link Collection} of {@link org.educoins.core.p2p.peers.FullPeer}s.
     * @throws DiscoveryException whenever the discovery failed.
     */
    Collection<Peer> getFullPeers() throws DiscoveryException;

    /**
     * Discovers the specific {@link org.educoins.core.p2p.peers.ReadOnlyPeer}s.
     *
     * @return {@link Collection} of {@link org.educoins.core.p2p.peers.ReadOnlyPeer}s.
     * @throws DiscoveryException whenever the discovery failed.
     */
    Collection<Peer> getReadOnlyPeers() throws DiscoveryException;
}
