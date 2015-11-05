package org.educoins.core.p2p.peers;

import org.educoins.core.p2p.peers.remote.RemoteNode;

/**
 * The {@link Peer}-type representing a Peer with full capabilities.
 * Created by typus on 11/3/15.
 */
public class FullPeer extends Peer {
    public FullPeer(RemoteNode remoteNode) {
        super(remoteNode);
    }
}
