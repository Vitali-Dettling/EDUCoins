package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;

/**
 * The Reference Client consisting of a Miner, a {@link BlockChain} and a {@link Wallet}.
 * Created by typus on 11/23/15.
 */
public class ReferencePeer extends Peer {

    private final Wallet wallet;
    
    public ReferencePeer(BlockChain blockChain, Wallet wallet) {
        this.blockChain = blockChain;
        this.wallet = wallet;
        
    }
    
    @Override
    public void start() throws DiscoveryException {
        remoteProxies.discover();
    }

//    public ReferencePeer(IProxyPeerGroup remoteProxies, Miner miner, BlockChain blockChain, Wallet wallet) {
//        super(remoteProxies);
//        this.miner = miner;
//        this.blockChain = blockChain;
//        this.wallet = wallet;
//    }
}
