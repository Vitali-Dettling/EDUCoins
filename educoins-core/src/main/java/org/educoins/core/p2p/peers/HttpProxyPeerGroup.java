package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.*;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An {@link IProxyPeerGroup} of {@link HttpProxyPeerGroup}.
 */
@Component
@Scope("singleton")
public class HttpProxyPeerGroup implements IProxyPeerGroup {
    private final Logger logger = LoggerFactory.getLogger(HttpProxyPeerGroup.class);

    private List<RemoteProxy> proxies = new CopyOnWriteArrayList<>();
    private Set<IBlockListener> blockListeners = new HashSet<>();
    private Set<ITransactionListener> transactionListeners = new HashSet<>();

    @Override
    public void addProxy(RemoteProxy proxy) {
        this.proxies.add(proxy);
    }

    @Override
    public void clearProxies() {
        this.proxies.clear();
    }

    @Override
    public boolean containsProxy(RemoteProxy proxy) {
        return this.proxies.contains(proxy);
    }

    @Override
    public void discover(DiscoveryStrategy strategy) throws DiscoveryException {
        logger.info("Starting new Discovery ({})", strategy.getClass().getName());
        strategy.getReferencePeers().forEach(proxy -> proxies.add(proxy));
        proxies.forEach(proxy -> {
            try {
                proxies.addAll(proxy.hello());
            } catch (IOException e) {
                logger.warn("Could not say Hello to {}@{}", proxy.getPubkey(), proxy.getiNetAddress());
            }
        });
    }

    @Override
    public Collection<RemoteProxy> getAllProxies() {
        Set<RemoteProxy> proxies = new HashSet<>();
        proxies.addAll(this.proxies);
        return proxies;
    }

    @Override
    public void transmitTransaction(Transaction transaction) {
    }

    //region listeners
    @Override
    public void addBlockListener(IBlockListener blockListener) {
        blockListeners.add(blockListener);
    }

    @Override
    public void removeBlockListener(IBlockListener blockListener) {
        blockListeners.remove(blockListener);
    }

    @Override
    public void receiveBlocks() {
        for (RemoteProxy proxy : getHighestRatedProxies()) {
            try {
                proxy.getBlocks().parallelStream().forEach(block -> blockListeners.
                        forEach(iBlockListener -> iBlockListener.blockReceived(block)));

                proxy.rateHigher();
            } catch (IOException e) {
                if (checkProxiesState(proxy, e)) return;
                logger.error("Could not retrieve Blocks", e);
            }
        }
    }

    @Override
    public void addTransactionListener(ITransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    @Override
    public void removeTransactionListener(ITransactionListener transactionListener) {
        transactionListeners.remove(transactionListener);
    }
    //endregion

    @Override
    public void receiveTransactions() {
        for (RemoteProxy proxy : getHighestRatedProxies()) {
            try {
                proxy.getBlocks().parallelStream().
                        forEach(block -> block.getTransactions().
                                forEach(transaction -> transactionListeners.
                                        forEach(iTransactionListener ->
                                                iTransactionListener.transactionReceived(transaction))));

                proxy.rateHigher();
            } catch (IOException e) {
                if (checkProxiesState(proxy, e)) return;
                logger.error("Could not retrieve Blocks", e);
            }
        }
    }

    //region getter/setter
    public Set<IBlockListener> getBlockListeners() {
        return blockListeners;
    }

    public void setBlockListeners(Set<IBlockListener> blockListeners) {
        this.blockListeners = blockListeners;
    }

    public Set<ITransactionListener> getTransactionListeners() {
        return transactionListeners;
    }

    public void setTransactionListeners(Set<ITransactionListener> transactionListeners) {
        this.transactionListeners = transactionListeners;
    }
    //endregion

    private boolean checkProxiesState(RemoteProxy proxy, IOException e) {
        // Proxy should no longer be part of the peer group because it failed to respond in any way.
        proxy.rateLower();

        if (proxy.getRating() <= 0) {
            proxies.remove(proxy);
            logger.info("Removed Proxy from peer group {}@{}", proxy.getPubkey(), proxy
                    .getiNetAddress().getHost(), e);

            if (proxies.size() == 0)
                rediscover(0);
            return true;
        }
        return false;
    }


    private Collection<RemoteProxy> getHighestRatedProxies() {
        if (proxies.size() == 0) {
            rediscover(0);
            return proxies;
        }

        Collections.sort(proxies, (o1, o2) -> o1.getRating() - o2.getRating());
        return proxies.subList(0, Math.min(proxies.size(), 10));
    }

    private void rediscover(int nTry) {
        try {
            discover(new CentralDiscovery());
        } catch (DiscoveryException e1) {
            if (nTry < 5)
                try {
                    Thread.sleep(nTry * 2000);
                } catch (InterruptedException e) {
                }

            rediscover(++nTry);
            logger.error("Could not retrieve any Peers... We are isolated now!");
        }
    }
}
