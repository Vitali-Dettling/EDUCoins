package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;

/**
 * The {@link Peer}-Type having only reading-capabilities.
 * Created by typus on 11/3/15.
 */
public class SoloMinerPeer extends Peer {
    //    private final Miner miner;
    private final BlockChain blockChain;

    public SoloMinerPeer(BlockChain blockChain) {
        this.blockChain = blockChain;
    }
}
