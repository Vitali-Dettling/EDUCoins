package org.educoins.core.p2p.peers;

import org.educoins.core.*;

/**
 * The Reference Client consisting of a Miner, a {@link BlockChain} and a {@link Wallet}.
 * Created by typus on 11/23/15.
 */
public class ReferencePeer extends Peer {
    private final Miner miner;
    private final Wallet wallet;

    public ReferencePeer(BlockChain blockChain, Miner miner, Wallet wallet) {
        this.blockChain = blockChain;
        this.miner = miner;
        this.wallet = wallet;
    }

    public ReferencePeer(IProxyPeerGroup remoteProxies, Miner miner, BlockChain blockChain, Wallet wallet) {
        super(remoteProxies);
        this.miner = miner;
        this.blockChain = blockChain;
        this.wallet = wallet;
    }
}
