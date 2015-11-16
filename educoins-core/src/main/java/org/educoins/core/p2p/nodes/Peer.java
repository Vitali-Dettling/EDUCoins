package org.educoins.core.p2p.nodes;

import java.util.Collection;

import org.educoins.core.Block;

/**
 * A PeerNode representation. Necessary for P2P Networking.
 * Created by typus on 10/27/15.
 */
public interface Peer {
    Collection<Block> getBlocks();
}
