package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;

/**
 * The {@link Peer}-type representing a Peer with full capabilities.
 * Created by typus on 11/3/15.
 */
public class FullBlockChainPeer extends Peer {
    ;

    public FullBlockChainPeer(BlockChain blockChain) {
        this.blockChain = blockChain;
    }
}
