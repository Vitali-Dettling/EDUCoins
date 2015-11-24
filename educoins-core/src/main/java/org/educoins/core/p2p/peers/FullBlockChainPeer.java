package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link Peer}-type representing a Peer with full capabilities.
 * Created by typus on 11/3/15.
 */
public class FullBlockChainPeer extends Peer {

    private final BlockChain blockChain;

    public FullBlockChainPeer(@NotNull RemoteNode remoteNode, @NotNull BlockChain blockChain) {
        super(remoteNode);
        this.blockChain = blockChain;
    }

    public FullBlockChainPeer(@NotNull RemoteNode remoteNode) {
        super(remoteNode);
        this.blockChain = null;
    }
}
