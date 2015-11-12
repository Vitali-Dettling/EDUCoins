package org.educoins.core.p2p.peers.remote;


import org.educoins.core.Block;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.utils.RestClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

/**
 * The Representation of a {@link Peer} communicating via HTTP.
 * Created by typus on 10/27/15.
 */
public class HttpNode extends RemoteNode {

    @Override
    @NotNull
    public Collection<Block> getHeaders() throws IOException {
        return Arrays.asList(new RestClient<Block[]>().get(URI.create(uri.toString() + "/blocks/headers"), Block[].class));
    }

    @Override
    @NotNull
    public Collection<Block> getBlocks() throws IOException {
        return Arrays.asList(new RestClient<Block[]>().get(URI.create(uri.toString() + "/blocks/"), Block[].class));
    }
}
