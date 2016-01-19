package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.discovery.DiscoveryStrategy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

/**
 * An {@link IProxyPeerGroup} of {@link HttpProxyPeerGroup}.
 */
public class HttpProxyPeerGroup implements IProxyPeerGroup {
    private final Logger logger = LoggerFactory.getLogger(HttpProxyPeerGroup.class);

    private Set<IBlockListener> blockListeners = new HashSet<>();
    private Set<ITransactionListener> transactionListeners = new HashSet<>();
    private ProxySet proxySet;

    /**
     * If set, {@link #receiveBlocks(Sha256Hash)} will recurse if no blocks were
     * received.
     */
    private boolean retry = false;

    @Autowired
    public HttpProxyPeerGroup(@NotNull ProxySet proxySet) {
        this.proxySet = proxySet;
    }

    @Override
    public void addProxy(RemoteProxy proxy) {
        this.proxySet.addProxy(proxy);
    }

    @Override
    public void clearProxies() {
        this.proxySet.clear();
    }

    @Override
    public boolean containsProxy(RemoteProxy proxy) {
        return this.proxySet.contains(proxy);
    }

    @Override
    public void discoverOnce(DiscoveryStrategy strategy) throws DiscoveryException {
        logger.info("Starting new Discovery ({})", strategy.getClass().getName());
        strategy.getPeers().forEach(this::addProxy);
        if (proxySet.size() == 0)
            throw new DiscoveryException("No proxies received!");
    }

    @Override
    public void discover() {
        proxySet.discover();
    }

    @Override
    public Collection<RemoteProxy> getAllProxies() {
        return proxySet.getAllProxies();
    }

    @Override
    public void transmitTransaction(Transaction transaction) {
        proxySet.getProxiesToCommunicate().forEach(proxy -> {
            try {
                logger.info("Sending transaction to {}@{}", proxy.getPubkey(), proxy.getiNetAddress());
                proxy.transmitTransaction(transaction);
                proxySet.onCommunicationSuccess(proxy);
            } catch (IOException e) {
                logger.warn("Could not transmit block to {}@{]", proxy.getPubkey(), proxy.getiNetAddress().getHost(),
                        e);
                proxySet.onCommunicationFailure(proxy);
            }
        });
        logger.info("Transaction transmission done.");
    }

    @Override
    public void blockReceived(Block block) {
        logger.info("Dispatching the new Block ({})...", block.hash());
        proxySet.getProxiesToCommunicate().forEach(proxy -> {
            try {
                logger.info("Dispatching to {}@{}", proxy.getPubkey(), proxy.getiNetAddress());
                proxy.transmitBlock(block);
                proxySet.onCommunicationSuccess(proxy);
            } catch (IOException e) {
                logger.warn("Could not transmit block to {}@{}", proxy.getPubkey(), proxy.getiNetAddress().getHost(), e);
                proxySet.onCommunicationFailure(proxy);
            }
        });
        logger.info("Dispatching done.", block.hash());
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
        for (RemoteProxy proxy : proxySet.getProxiesToCommunicate()) {
            try {
                Collection<Block> blocks = proxy.getBlocks(from);
                blocksReceived += blocks.size();
                logger.info("Received {} blocks from proxy {}@{}", blocks.size(), proxy.getPubkey(),
                        proxy.getiNetAddress());

                // TODO: parallel? -> No, because the verification needs the
                // block in a specific order, beginning from the genesis block.
                blocks.stream().forEach(
                        block -> blockListeners.forEach(iBlockListener -> iBlockListener.blockReceived(block)));

                proxySet.onCommunicationSuccess(proxy);
            } catch (IOException e) {
                logger.error("Could not retrieve Blocks from proxy: {}@{}", proxy.getPubkey(), proxy.getiNetAddress(),
                        e);
                proxySet.onCommunicationFailure(proxy);
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

    @Override
    public void receiveTransactions() {
        logger.info("Receiving Transactions now...");
        for (RemoteProxy proxy : proxySet.getProxiesToCommunicate()) {
            try {
                proxy.getBlocks()
                        .forEach(block -> block.getTransactions().forEach(transaction -> transactionListeners.forEach(
                                TransactionListener -> TransactionListener.transactionReceived(transaction))));

                proxySet.onCommunicationSuccess(proxy);
            } catch (IOException e) {
                proxySet.onCommunicationFailure(proxy);
                logger.error("Could not retrieve Blocks from proxy: {}@{}", proxy.getPubkey(), proxy.getiNetAddress(),
                        e);
            }
        }
        logger.info("Receiving Transactions successful.");
    }
    // endregion

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

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }
}
