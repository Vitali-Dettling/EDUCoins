package org.educoins.core.p2p.peers.remote;


import org.educoins.core.Block;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.p2p.peers.server.BlockServer;
import org.educoins.core.utils.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return Arrays.asList(
                new RestClient<Block[]>()
                        .get(URI.create(iNetAddress.toString() + BlockServer.BLOCK_HEADERS_RESOURCE_PATH),
                                Block[].class));
    }

    @Override
    @NotNull
    public Collection<Block> getBlocks() throws IOException {
        return Arrays.asList(
                new RestClient<Block[]>()
                        .get(URI.create(iNetAddress.toString() + BlockServer.BLOCKS_RESOURCE_PATH),
                                Block[].class));
    }

    @Override
    @Nullable
    public Block getBlock(Sha256Hash hash) throws IOException {
        try {
            return new RestClient<Block>()
                    .get(URI.create(iNetAddress.toString() + BlockServer.BLOCKS_RESOURCE_PATH + hash.toString()),
                            Block.class);
        } catch (HttpException ex) {
            if (ex.getStatus() == 404) return null;
            throw ex;
        }
    }
}