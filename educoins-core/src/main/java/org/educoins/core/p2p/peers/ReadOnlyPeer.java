package org.educoins.core.p2p.peers;

import org.educoins.core.p2p.peers.remote.RemoteNode;

/**
 * The {@link Peer}-Type having only reading-capabilities.
 * Created by typus on 11/3/15.
 */
public class ReadOnlyPeer extends Peer {
    public ReadOnlyPeer(RemoteNode remoteNode) {
        super(remoteNode);
    }
}
