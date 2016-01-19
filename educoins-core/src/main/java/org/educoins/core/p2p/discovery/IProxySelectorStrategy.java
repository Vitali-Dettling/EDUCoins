package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.remote.RemoteProxy;

import java.util.Collection;

/**
 * The Strategy interface to provide a
 * {@link RemoteProxy} selection for  {@link org.educoins.core.p2p.peers.IProxyPeerGroup}s to work with.
 * Created by typus on 1/19/16.
 */
public interface IProxySelectorStrategy {
    /**
     * Returns a subset of the given {@link RemoteProxy}s to communicate with. Which get chosen is implementation
     * specific.
     *
     * @param allKnownProxies the proxies you know so far.
     * @return a subset of <code>allKnownProxies</code>.
     */
    Collection<RemoteProxy> getProxies(Collection<RemoteProxy> allKnownProxies);
}
