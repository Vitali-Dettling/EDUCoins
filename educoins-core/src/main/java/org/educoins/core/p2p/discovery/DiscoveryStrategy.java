package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.nodes.Peer;

import java.util.Collection;

/**
 * The interface providing functionality to retrieve the necessary {@link Peer}s.
 * This interface should represent the Strategy-Pattern.
 * Created by typus on 10/27/15.
 */
public interface DiscoveryStrategy {
    Collection<Peer> getPeers();
}
