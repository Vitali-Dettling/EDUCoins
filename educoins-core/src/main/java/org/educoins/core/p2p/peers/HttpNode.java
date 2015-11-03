package org.educoins.core.p2p.peers;


import org.educoins.core.Block;

import java.util.Collection;

/**
 * The Representation of a {@link Peer} communicating via HTTP.
 * Created by typus on 10/27/15.
 */
public class HttpNode extends RemoteNode {

    @Override
    public Collection<Block> getBlocks() {
        //TODO: implement http call using restClient; think about webservice for nodes.
        return null;
    }
}
