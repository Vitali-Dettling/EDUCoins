package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.Wallet;

/**
 * The Reference Client consisting of a Miner, a {@link BlockChain} and a {@link Wallet}.
 * Created by typus on 11/23/15.
 */
public class ReferencePeer extends Peer {
    //    private final Miner miner;
    private final BlockChain blockChain;
    private final Wallet wallet;

    public ReferencePeer(BlockChain blockChain, Wallet wallet) {
        this.blockChain = blockChain;
        this.wallet = wallet;
    }

    public ReferencePeer(IProxyPeerGroup remoteProxies, BlockChain blockChain, Wallet wallet) {
        super(remoteProxies);
        this.blockChain = blockChain;
        this.wallet = wallet;
    }
}
