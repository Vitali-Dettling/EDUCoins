package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.Wallet;
import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.jetbrains.annotations.NotNull;

/**
 * The Reference Client consisting of a Miner, a {@link BlockChain} and a {@link Wallet}.
 * Created by typus on 11/23/15.
 */
public class ReferencePeer extends Peer {
    //    private final Miner miner;
    private final BlockChain blockChain;
    private final Wallet wallet;

    public ReferencePeer(@NotNull RemoteNode remoteNode,
//                         @NotNull Miner miner,
                         @NotNull BlockChain blockChain,
                         @NotNull Wallet wallet) {
        super(remoteNode);
//        this.miner = miner;
        this.blockChain = blockChain;
        this.wallet = wallet;
    }

    public ReferencePeer(@NotNull RemoteNode remoteNode) {
        super(remoteNode);
//        this.miner = null;
        this.blockChain = null;
        this.wallet = null;
    }

}
