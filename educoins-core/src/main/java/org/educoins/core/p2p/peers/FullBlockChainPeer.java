package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link Peer}-type representing a Peer with full capabilities.
 * Created by typus on 11/3/15.
 */
public class FullBlockChainPeer extends Peer {

    private final BlockChain blockChain;

    public FullBlockChainPeer(@NotNull RemoteProxy remoteProxy, @NotNull BlockChain blockChain) {
        super(remoteProxy);
        this.blockChain = blockChain;
    }

    public FullBlockChainPeer(@NotNull RemoteProxy remoteProxy) {
        super(remoteProxy);
        this.blockChain = null;
    }
}
