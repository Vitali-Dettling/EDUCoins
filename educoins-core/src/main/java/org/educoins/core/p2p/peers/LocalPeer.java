package org.educoins.core.p2p.peers;

import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;

/**
 * The Peer used for Testing.
 * Created by typus on 11/5/15.
 */
public class LocalPeer extends Peer {
    public LocalPeer(@NotNull RemoteProxy remoteProxy) {
        super(remoteProxy);
    }
}
