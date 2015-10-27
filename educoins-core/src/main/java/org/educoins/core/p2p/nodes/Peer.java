package org.educoins.core.p2p.nodes;

import org.educoins.core.Block;

import java.util.Collection;

/**
 * A Peernode representation. Necessary for P2P Networking.
 * Created by typus on 10/27/15.
 */
public interface Peer {
    Collection<Block> getBlocks();
}
