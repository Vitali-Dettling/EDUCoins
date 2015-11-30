package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Reference Client->miner,blockchain,wallet
 * Full BlockChain->blockchain
 * Solo Miner->miner,blockchain
 **/

/**
 * A PeerNode representation. Necessary for P2P Networking.
 * Created by typus on 10/27/15.
 */
public abstract class Peer implements IBlockReceiver, ITransactionReceiver, ITransactionTransmitter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected Set<IBlockListener> blockListeners = new HashSet<>();
    protected Set<ITransactionListener> transactionListeners = new HashSet<>();
    protected RemoteNode remoteNode;


    public Peer(@NotNull RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }

    @NotNull
    public Collection<Block> getBlocks() throws IOException {
        return remoteNode.getBlocks();
    }

    @NotNull
    public Collection<Block> getHeaders() throws IOException {
        return remoteNode.getHeaders();
    }

    @NotNull
    public RemoteNode getRemoteNode() {
        return remoteNode;
    }

    public void setRemoteNode(@NotNull RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }

    @Override
    public int hashCode() {
        return remoteNode != null ? remoteNode.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        return !(remoteNode != null ? !remoteNode.equals(peer.remoteNode) : peer.remoteNode != null);
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

    }

    @Override
    public void transmitTransaction(Transaction transaction) {

    }
}
