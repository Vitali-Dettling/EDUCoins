package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.discovery.*;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.educoins.core.utils.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An {@link IProxyPeerGroup} of {@link HttpProxyPeerGroup}.
 */
//@Component
//@Scope("singleton")
public class HttpProxyPeerGroup implements IProxyPeerGroup {
    private final Logger logger = LoggerFactory.getLogger(HttpProxyPeerGroup.class);

    private List<RemoteProxy> proxies = new CopyOnWriteArrayList<>();
    private Set<IBlockListener> blockListeners = new HashSet<>();
    private Set<ITransactionListener> transactionListeners = new HashSet<>();

    @Override
    public void addProxy(RemoteProxy proxy) {
        if (!proxies.contains(proxy)
                && !proxy.getPubkey().equals(AppConfig.getOwnPublicKey().toString())
                && proxies.size() < 100) {
            logger.info("Added peer " + proxy);
            this.proxies.add(proxy);
        }
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
        strategy.getPeers().forEach(this::addProxy);
        if (proxies.size() == 0)
            throw new DiscoveryException("No proxies received!");
        proxies.parallelStream().forEach(proxy -> {
            try {
                proxy.hello().forEach(this::addProxy);
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
    public void discover() {
        try {
            new CentralDiscovery().hello();
        } catch (DiscoveryException e) {
            logger.warn("Could not hello the Central!", e);
        }
        rediscover(0);
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
    public void receiveBlocks(Sha256Hash from) {
        logger.info("Receiving blocks now...");
        long blocksReceived = 0;
        for (RemoteProxy proxy : getHighestRatedProxies()) {
            try {
                Collection<Block> blocks = proxy.getBlocks(from);
                blocksReceived += blocks.size();
                logger.info("Received {} blocks from proxy {}@{}",
                        blocks.size(), proxy.getPubkey(), proxy.getiNetAddress());

                //TODO: parallel?
                blocks.stream().forEach(block -> blockListeners.
                        forEach(iBlockListener -> iBlockListener.blockReceived(block)));

                proxy.rateHigher();
            } catch (IOException e) {
                if (checkProxiesState(proxy, e)) return;
                logger.error("Could not retrieve Blocks from proxy: {}@{}",
                        proxy.getPubkey(), proxy.getiNetAddress(), e);
            }
        }
        //RETRY
        if (blocksReceived == 0) {
            logger.info("Did not retrieve any blocks... Retry in 1 minute");
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
            }
            logger.info("Retrying now.");
            receiveBlocks(from);
        }
        logger.info("Receiving blocks done.");
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
        logger.info("Receiving Transactions now.");
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
                logger.error("Could not retrieve Blocks from proxy: {}@{}",
                        proxy.getPubkey(), proxy.getiNetAddress(), e);
            }
        }
        logger.info("Receiving Transactions successful.");
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
                discover();
            return true;
        }
        return false;
    }


    private Collection<RemoteProxy> getHighestRatedProxies() {
        if (proxies.size() == 0) {
            discover();
            return proxies;
        }

        Collections.sort(proxies, (o1, o2) -> o1.getRating() - o2.getRating());
        return proxies.subList(0, Math.min(proxies.size(), 10));
    }

    public void rediscover(int nTry) {
        try {
            discover(new CentralDiscovery());
        } catch (DiscoveryException e1) {
            logger.error("Could not retrieve any Peers... We are isolated now!");
            if (nTry < AppConfig.getMaxDiscoveryRetries())
                try {
                    Thread.sleep(nTry * 2000);
                    rediscover(++nTry);
                } catch (InterruptedException e) {
                }
        }
    }

    @Override
    public void foundPoW(Block block) {
        getHighestRatedProxies().forEach(proxy -> {
            try {
                proxy.transmitBlock(block);
            } catch (IOException e) {
                logger.warn("Could not transmit block to {}@{]", proxy.getPubkey(), proxy
                        .getiNetAddress().getHost(), e);
            }
        });
    }
}
