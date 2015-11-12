package org.educoins.core.p2p.peers;

import org.educoins.core.Block;
import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

/**
 * A PeerNode representation. Necessary for P2P Networking.
 * Created by typus on 10/27/15.
 */
public abstract class Peer {

    protected RemoteNode remoteNode;

    public Peer(@NotNull RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }

    @NotNull
    public Collection<Block> getBlocks() throws IOException {
        return remoteNode.getBlocks();
    }

    @NotNull
    public RemoteNode getRemoteNode() {
        return remoteNode;
    }

    public void setRemoteNode(@NotNull RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }

    @Override
    public int hashCode() {
        return remoteNode != null ? remoteNode.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        return !(remoteNode != null ? !remoteNode.equals(peer.remoteNode) : peer.remoteNode != null);
    }
}
