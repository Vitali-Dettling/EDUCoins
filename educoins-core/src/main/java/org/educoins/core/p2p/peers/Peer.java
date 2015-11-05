package org.educoins.core.p2p.peers;

import org.educoins.core.Block;
import org.educoins.core.p2p.peers.remote.RemoteNode;

import java.io.IOException;
import java.util.Collection;

/**
 * A PeerNode representation. Necessary for P2P Networking.
 * Created by typus on 10/27/15.
 */
public abstract class Peer {

    protected RemoteNode remoteNode;

    public Peer(RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }

    public Collection<Block> getBlocks() throws IOException {
        return remoteNode.getBlocks();
    }

    public RemoteNode getRemoteNode() {
        return remoteNode;
    }

    public void setRemoteNode(RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }
}
