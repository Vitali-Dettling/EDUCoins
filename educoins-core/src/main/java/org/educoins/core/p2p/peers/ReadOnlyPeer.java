package org.educoins.core.p2p.peers;

import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link Peer}-Type having only reading-capabilities.
 * Created by typus on 11/3/15.
 */
public class ReadOnlyPeer extends Peer {
    public ReadOnlyPeer(@NotNull RemoteNode remoteNode) {
        super(remoteNode);
    }
}
