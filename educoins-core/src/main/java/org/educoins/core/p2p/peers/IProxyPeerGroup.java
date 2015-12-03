package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.discovery.DiscoveryStrategy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;

import java.util.Collection;

/**
 * A Group of Peers represented as {@link RemoteProxy}s. This unit handles discovery mechanisms autonomously.
 * It is also
 * {@link IBlockReceiver}, {@link ITransactionReceiver} and {@link ITransactionTransmitter} to enable {@link Peer}s
 * to handle Networking.
 * Created by typus on 12/3/15.
 */
public interface IProxyPeerGroup extends IBlockReceiver, ITransactionReceiver, ITransactionTransmitter {

    /**
     * Adds a {@link RemoteProxy} to the PeerGroup.
     *
     * @param proxy the proxy to add.
     */
    void addProxy(RemoteProxy proxy);

    /**
     * Clears all {@link RemoteProxy}s saved in the PeerGroup.
     */
    void clearProxies();

    /**
     * Does a lookup for a specific {@link RemoteProxy} in the PeerGroup.
     *
     * @param proxy the specific proxy.
     * @return true if the proxy is present.
     */
    boolean containsProxy(RemoteProxy proxy);

    /**
     * Starts discovery with the given
     * {@link DiscoveryStrategy}. Afterwards {@link RemoteProxy#hello()}s all {@link Peer}s and
     * adds their known {@link RemoteProxy}s to the PeerGroup.
     *
     * @param strategy the kind of discovery which should be executed.
     * @throws DiscoveryException if the discovery went wrong.
     */
    void discover(DiscoveryStrategy strategy) throws DiscoveryException;

    /**
     * Returns all {@link RemoteProxy}s of the PeerGroup.
     *
     * @return a copy.
     */
    Collection<RemoteProxy> getAllProxies();
}
