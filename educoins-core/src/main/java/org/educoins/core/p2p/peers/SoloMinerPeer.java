package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link Peer}-Type having only reading-capabilities.
 * Created by typus on 11/3/15.
 */
public class SoloMinerPeer extends Peer {
    //    private final Miner miner;
    private final BlockChain blockChain;


    public SoloMinerPeer(@NotNull RemoteNode remoteNode,
//                         @NotNull Miner miner,
                         @NotNull BlockChain blockChain) {
        super(remoteNode);
//        this.miner = miner;
        this.blockChain = blockChain;
    }

    public SoloMinerPeer(@NotNull RemoteNode remoteNode) {
        super(remoteNode);
//        this.miner = null;
        this.blockChain = null;
    }
}
