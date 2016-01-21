package org.educoins.core.p2p.peers;

import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.discovery.*;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a set of {@link RemoteProxy}s. Also manages the use of {@link DiscoveryStrategy}s.
 * Created by typus on 1/19/16.
 */
@Component
public class ProxySet {

    protected final static Logger logger = LoggerFactory.getLogger(ProxySet.class);
    protected final ConcurrentHashMap<String, RemoteProxy> proxies = new ConcurrentHashMap<>();
    protected final int minDiscoveryDelayInMinutes = 30;
    protected IProxySelectorStrategy proxySelectorStrategy;
    protected DateTime lastDiscovery = DateTime.now().minusDays(1);

    @Autowired
    public ProxySet(IProxySelectorStrategy proxySelectorStrategy) {
        this.proxySelectorStrategy = proxySelectorStrategy;
    }

    public ProxySet() {
        this(new TopTenProxySelector());
    }

    public Collection<RemoteProxy> getProxiesToCommunicate() {
        if (proxies.size() == 0) {
            // Otherwise each miner thread will try to register itself
            discover();
            return getAllProxies();
        }
        return proxySelectorStrategy.getProxies(getAllProxies());
    }

    public void discoverOnce(DiscoveryStrategy strategy) throws DiscoveryException {
        logger.info("Starting new Discovery ({})", strategy.getClass().getName());
        strategy.getPeers().forEach(this::addProxy);
        if (proxies.size() == 0)
            throw new DiscoveryException("No proxies received!");
        lastDiscovery = DateTime.now();
    }

    public void discover() {
        if (lastDiscovery.plusMinutes(minDiscoveryDelayInMinutes).isAfterNow()) return;

        try {
            new CentralDiscovery().hello();
        } catch (DiscoveryException e) {
            logger.warn("Could not hello the Central!", e);
        }
        rediscover(0);
        lastDiscovery = DateTime.now();
    }

    public void onCommunicationFailure(RemoteProxy proxy) {
        RemoteProxy proxyFromOwnSet = proxies.get(proxy.getPubkey());
        if (proxyFromOwnSet != null)
            checkProxiesState(proxyFromOwnSet);
    }

    public void onCommunicationSuccess(RemoteProxy proxy) {
        RemoteProxy proxyFromOwnSet = proxies.get(proxy.getPubkey());
        if (proxyFromOwnSet != null)
            proxyFromOwnSet.rateHigher();
    }

    public void addProxy(RemoteProxy proxy) {
        if (!proxies.contains(proxy) && !proxy.getPubkey().equals(AppConfig.getOwnPublicKey().toString())
                && proxies.size() < 100) {
            logger.info("Added peer " + proxy);

            this.proxies.put(proxy.getPubkey(), proxy);
        }
    }

    public void clear() {
        this.proxies.clear();
    }


    public boolean contains(RemoteProxy proxy) {
        return this.proxies.contains(proxy);
    }

    public int size() {
        return proxies.size();
    }

    public Collection<RemoteProxy> getAllProxies() {
        Set<RemoteProxy> proxies = new HashSet<>();
        proxies.addAll(this.proxies.values());
        return proxies;
    }

    private void checkProxiesState(@NotNull RemoteProxy proxy) {
        // Proxy should no longer be part of the peer group because it failed to
        // respond in any way.
        proxy.rateLower();

        if (proxy.getRating() <= 0) {
            proxies.remove(proxy.getPubkey());
            logger.info("Removed Proxy from peer group {}@{}", proxy.getPubkey(), proxy.getiNetAddress().getHost());

            if (proxies.size() == 0)
                discover();
        }
    }

    public void rediscover(int nTry) {
        try {
            discoverOnce(new CentralDiscovery());
            discoverOnce(new PeerDiscovery(getAllProxies()));

        } catch (DiscoveryException e1) {
            if (nTry < AppConfig.getMaxDiscoveryRetries() && proxies.size() == 0) {
                logger.error("Could not retrieve any Peers... We are isolated now!");
                try {
                    // escalation (3secs, 6secs, 12secs...)
                    Thread.sleep(nTry * 3000);
                    rediscover(++nTry);
                } catch (InterruptedException e) {
                    logger.debug("Sleep interrupted!?", e);
                }
            }
        }
    }


}
