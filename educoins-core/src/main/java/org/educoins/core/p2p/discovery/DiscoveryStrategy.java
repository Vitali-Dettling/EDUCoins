package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.Peer;
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
     * Discovers the specific {@link Peer}s.
     *
     * @return a {@link Collection} of {@link Peer}s represented by {@link RemoteProxy}.
     * @throws DiscoveryException whenever the discovery failed.
     */
    @NotNull Collection<RemoteProxy> getPeers() throws DiscoveryException;
}

