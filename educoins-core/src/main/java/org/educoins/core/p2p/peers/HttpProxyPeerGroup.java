package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.discovery.*;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An {@link IProxyPeerGroup} of {@link HttpProxyPeerGroup}.
 */
public class HttpProxyPeerGroup implements IProxyPeerGroup {
    private final Logger logger = LoggerFactory.getLogger(HttpProxyPeerGroup.class);

    private List<RemoteProxy> proxies = new CopyOnWriteArrayList<>();
    private Set<IBlockListener> blockListeners = new HashSet<>();
    private Set<ITransactionListener> transactionListeners = new HashSet<>();
    /**
     * If set, {@link #receiveBlocks(Sha256Hash)} will recurse if no blocks were
     * received.
     */
    private boolean retry = true;

    @Override
    public void addProxy(RemoteProxy proxy) {
        if (!proxies.contains(proxy) && !proxy.getPubkey().equals(AppConfig.getOwnPublicKey().toString())
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
    public void discoverOnce(DiscoveryStrategy strategy) throws DiscoveryException {
        logger.info("Starting new Discovery ({})", strategy.getClass().getName());
        strategy.getPeers().forEach(this::addProxy);
        if (proxies.size() == 0)
            throw new DiscoveryException("No proxies received!");
    }

    @Override
    public void discover() {
        try {
       		new CentralDiscovery().hello();
        } catch (DiscoveryException e) {
            logger.warn("Could not hello the Central!");
            logger.debug("", e);
        }
        rediscover(0);
    }

    @Override
    public Collection<RemoteProxy> getAllProxies() {
        Set<RemoteProxy> proxies = new HashSet<>();
        proxies.addAll(this.proxies);
        return proxies;
    }

    @Override
    public void transmitTransaction(Transaction transaction) {
    	Collection<RemoteProxy> proxies = getHighestRatedProxies();
    	proxies.forEach(proxy -> {
            try {
                logger.info("Sending transaction to {}@{}", proxy.getPubkey(), proxy.getiNetAddress());
                proxy.transmitTransaction(transaction);
            } catch (IOException e) {
                logger.warn("Could not transmit transaction to {}@{}", proxy.getPubkey(), proxy.getiNetAddress().getHost());
                logger.debug("", e);
            }
        });
    }

    // region listeners
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
                logger.info("Received {} blocks from proxy {}@{}", blocks.size(), proxy.getPubkey(),
                        proxy.getiNetAddress());

                // TODO: parallel? -> No, because the verification needs the
                // block in a specific order, beginning from the genesis block.
                blocks.stream().forEach(
                        block -> blockListeners.forEach(iBlockListener -> iBlockListener.blockListener(block)));

                proxy.rateHigher();
            } catch (IOException e) {
                if (checkProxiesState(proxy, e))
                    return;
                logger.error("Could not retrieve Blocks from proxy: {}@{}", proxy.getPubkey(), proxy.getiNetAddress());
                logger.debug("", e);
                retry(blocksReceived, from);
            }
        }
        logger.info("Receiving blocks done.");
    }

    private void retry(long blocksReceived, Sha256Hash from) {
        // RETRY onyl if no peer are available right now.
        if (blocksReceived == 0 && retry) {
            logger.info("Did not retrieve any blocks... Retry in 1 minute");
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
            }
            logger.info("Retrying now.");
            receiveBlocks(from);
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
    // endregion

    @Override
    public void receiveTransactions() {
        logger.info("Receiving Transactions now.");
        for (RemoteProxy proxy : getHighestRatedProxies()) {
            try {
                proxy.getBlocks().parallelStream()
                        .forEach(block -> block.getTransactions().forEach(transaction -> transactionListeners.forEach(
                                TransactionListener -> TransactionListener.transactionReceived(transaction))));

                proxy.rateHigher();
            } catch (IOException e) {
                if (checkProxiesState(proxy, e))
                    return;
                logger.error("Could not retrieve Blocks from proxy: {}@{}", proxy.getPubkey(), proxy.getiNetAddress());
                logger.debug("", e);
            }
        }
        logger.info("Receiving Transactions successful.");
    }

    // region getter/setter
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
    // endregion

    private boolean checkProxiesState(RemoteProxy proxy, IOException e) {
        // Proxy should no longer be part of the peer group because it failed to
        // respond in any way.
        proxy.rateLower();

        if (proxy.getRating() <= 0) {
            proxies.remove(proxy);
            logger.info("Removed Proxy from peer group {}@{}", proxy.getPubkey(), proxy.getiNetAddress().getHost());
            logger.debug("", e);

            if (proxies.size() == 0)
                discover();
            return true;
        }
        return false;
    }

    private Collection<RemoteProxy> getHighestRatedProxies() {
        if (proxies.size() == 0) {
            // Otherwise each miner thread will try to register itself
            discover();
            return proxies;
        }

        Collections.sort(proxies, (o1, o2) -> {
            double val = o1.getRating() - o2.getRating();
            if (val > 0)
                return 1;
            else if (val < 0)
                return -1;
            return 0;
        });
        return proxies.subList(0, Math.min(proxies.size(), 10));
    }

    public void rediscover(int nTry) {
        try {
            discoverOnce(new CentralDiscovery());
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
        logger.info("Dispatching the new Block ({})...", block.hash());
        getHighestRatedProxies().forEach(proxy -> {
            try {
                logger.info("Dispatching to {}@{}", proxy.getPubkey(), proxy.getiNetAddress());
                proxy.transmitBlock(block);
            } catch (IOException e) {
                checkProxiesState(proxy, e);
                logger.warn("Could not transmit block to {}@{}", proxy.getPubkey(), proxy.getiNetAddress().getHost());
                logger.debug("", e);
            }
        });
        logger.info("Dispatching done.", block.hash());
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }
}
