package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.*;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The interface providing functionality to retrieve the necessary {@link Peer}s.
 * This interface should represent the Strategy-Pattern.
 * Created by typus on 10/27/15.
 */
public interface DiscoveryStrategy {

    /**
     * Makes the caller known to the target nodes.
     * @throws DiscoveryException whenever the hello-process failed.
     */
    void hello() throws DiscoveryException;

    /**
     * Discovers the specific {@link ReferencePeer}s.
     *
     * @return a {@link Collection} of {@link ReferencePeer}s.
     * @throws DiscoveryException whenever the discovery failed.
     */
    @NotNull Collection<RemoteProxy> getReferencePeers() throws DiscoveryException;

    /**
     * Discovers the specific {@link FullBlockChainPeer}s.
     *
     * @return a {@link Collection} of {@link FullBlockChainPeer}s.
     * @throws DiscoveryException whenever the discovery failed.
     */
    @NotNull Collection<RemoteProxy> getFullBlockchainPeers() throws DiscoveryException;

    /**
     * Discovers the specific {@link SoloMinerPeer}s.
     *
     * @return a {@link Collection} of {@link SoloMinerPeer}s.
     * @throws DiscoveryException whenever the discovery failed.
     */
    @NotNull Collection<RemoteProxy> getSoloMinerPeers() throws DiscoveryException;
}

