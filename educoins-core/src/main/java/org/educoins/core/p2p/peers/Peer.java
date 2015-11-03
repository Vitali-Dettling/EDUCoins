package org.educoins.core.p2p.peers;

import org.educoins.core.Block;

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

    public Collection<Block> getBlocks() {
        return remoteNode.getBlocks();
    }

    public RemoteNode getRemoteNode() {
        return remoteNode;
    }

    public void setRemoteNode(RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }
}
