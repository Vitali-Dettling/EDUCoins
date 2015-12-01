package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * A PeerNode representation. Necessary for P2P Networking. The concrete implementations are the following:
 * Reference Client->miner,blockchain,wallet
 * Full BlockChain->blockchain
 * Solo Miner->miner,blockchain
 * Created by typus on 10/27/15.
 */
public abstract class Peer implements IBlockReceiver, ITransactionReceiver, ITransactionTransmitter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Set<IBlockListener> blockListeners = new HashSet<>();
    protected Set<ITransactionListener> transactionListeners = new HashSet<>();
    protected RemoteProxy remoteProxy;

    public Peer() {
    }

    public Peer(@NotNull RemoteProxy remoteProxy) {
        this.remoteProxy = remoteProxy;
    }

    public void hello() throws IOException {
        remoteProxy.hello();
    }

    @NotNull
    public Collection<Block> getBlocks() throws IOException {
        return remoteProxy.getBlocks();
    }

    @NotNull
    public Collection<Block> getHeaders() throws IOException {
        return remoteProxy.getHeaders();
    }

    public @NotNull RemoteProxy getRemoteProxy() {
        return remoteProxy;
    }

    public void setRemoteProxy(@NotNull RemoteProxy remoteProxy) {
        this.remoteProxy = remoteProxy;
    }

    @Override
    public int hashCode() {
        return remoteProxy != null ? remoteProxy.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        return !(remoteProxy != null ? !remoteProxy.equals(peer.remoteProxy) : peer.remoteProxy != null);
    }

    @Override
    public void addBlockListener(IBlockListener blockListener) {
        this.blockListeners.add(blockListener);
    }

    @Override
    public void removeBlockListener(IBlockListener blockListener) {
        this.blockListeners.remove(blockListener);
    }

    @Override
    public void receiveBlocks() {
        try {
            getBlocks()
                    .forEach(block -> blockListeners
                            .forEach(iBlockListener -> iBlockListener.blockReceived(block)));
        } catch (IOException e) {
            logger.error("Could not receive Blocks", e);
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
        try {
            getBlocks().iterator().next().getTransactions();
        } catch (IOException e) {
            logger.error("Could not receive Transactions", e);
        }
    }

    @Override
    public void transmitTransaction(Transaction transaction) {

    }

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
}
