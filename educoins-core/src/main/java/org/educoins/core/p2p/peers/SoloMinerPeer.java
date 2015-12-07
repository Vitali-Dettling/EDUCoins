package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.Miner;

/**
 * The {@link Peer}-Type having only reading-capabilities.
 * Created by typus on 11/3/15.
 */
public class SoloMinerPeer extends Peer {
    private final Miner miner;
    private final BlockChain blockChain;

    public SoloMinerPeer(BlockChain blockChain, Miner miner) {
        this.blockChain = blockChain;
        this.miner = miner;
    }
}
