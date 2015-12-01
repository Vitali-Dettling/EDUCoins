package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link Peer}-Type having only reading-capabilities.
 * Created by typus on 11/3/15.
 */
public class SoloMinerPeer extends Peer {
    //    private final Miner miner;
    private final BlockChain blockChain;


    public SoloMinerPeer(@NotNull RemoteProxy remoteProxy,
//                         @NotNull Miner miner,
                         @NotNull BlockChain blockChain) {
        super(remoteProxy);
//        this.miner = miner;
        this.blockChain = blockChain;
    }

    public SoloMinerPeer(@NotNull RemoteProxy remoteProxy) {
        super(remoteProxy);
//        this.miner = null;
        this.blockChain = null;
    }
}
