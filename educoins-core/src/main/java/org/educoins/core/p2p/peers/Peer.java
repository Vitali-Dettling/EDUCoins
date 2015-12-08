package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.utils.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PeerNode representation. Necessary for P2P Networking. The concrete implementations are the following:
 * Reference Client->miner,blockchain,wallet
 * Full BlockChain->blockchain
 * Solo Miner->miner,blockchain
 * Created by typus on 10/27/15.
 */
public abstract class Peer implements IBlockReceiver, ITransactionReceiver, ITransactionTransmitter {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected IProxyPeerGroup remoteProxies = new HttpProxyPeerGroup();
    protected Sha256Hash publicKey;
    protected BlockChain blockChain;

    public Peer(IProxyPeerGroup remoteProxies) {
        this.remoteProxies = remoteProxies;
    }

    public Peer() {
        this.publicKey = AppConfig.getOwnPublicKey();
    }

    public void start() throws DiscoveryException {
        remoteProxies.discover();
        blockChain.foundPoW(new Block());
    }

    public void stop() {
        //TODO: anything to do?
    }

    @Override
    public void transmitTransaction(Transaction transaction) {
        remoteProxies.transmitTransaction(transaction);
    }

    //region listeners
    @Override
    public void addBlockListener(IBlockListener blockListener) {
        this.remoteProxies.addBlockListener(blockListener);
    }

    @Override
    public void removeBlockListener(IBlockListener blockListener) {
        this.remoteProxies.removeBlockListener(blockListener);
    }

    @Override
    public void receiveBlocks(Sha256Hash from) {
        this.remoteProxies.receiveBlocks(from);
    }

    @Override
    public void addTransactionListener(ITransactionListener transactionListener) {
        this.remoteProxies.addTransactionListener(transactionListener);
    }

    @Override
    public void removeTransactionListener(ITransactionListener transactionListener) {
        this.remoteProxies.removeTransactionListener(transactionListener);
    }

    @Override
    public void receiveTransactions() {
        remoteProxies.receiveTransactions();
    }
    //endregion

    @Override
    public int hashCode() {
        int result = logger != null ? logger.hashCode() : 0;
        result = 31 * result + (remoteProxies != null ? remoteProxies.hashCode() : 0);
        return result;
    }

    //region equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        return !(logger != null ? !logger.equals(peer.logger) : peer.logger != null)
                && !(remoteProxies != null ? !remoteProxies.equals(peer.remoteProxies) : peer.remoteProxies != null);

    }
    //endregion
}
