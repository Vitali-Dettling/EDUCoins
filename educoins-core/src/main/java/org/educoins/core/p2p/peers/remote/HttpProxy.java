package org.educoins.core.p2p.peers.remote;


import org.educoins.core.Block;
import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.p2p.peers.server.PeerServer;
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
public class HttpProxy extends RemoteProxy {

    public static final String PROTOCOL = "http://";

    private int ownPort = AppConfig.getOwnPort();
    private Sha256Hash ownPublicKey = AppConfig.getOwnPublicKey();

    public HttpProxy() {
    }

    public HttpProxy(@NotNull URI iNetAddress, @NotNull String pubkey) {
        super(iNetAddress, pubkey);
    }

    @Override
    @NotNull
    public Collection<Block> getHeaders() throws IOException {
        return Arrays.asList(
                new RestClient<Block[]>()
                        .get(URI.create(iNetAddress.toString() + PeerServer.BLOCK_HEADERS_RESOURCE_PATH),
                                Block[].class));
    }

    @Override
    @NotNull
    public Collection<Block> getBlocks() throws IOException {
        return Arrays.asList(
                new RestClient<Block[]>()
                        .get(URI.create(iNetAddress.toString() + PeerServer.BLOCKS_RESOURCE_PATH),
                                Block[].class));
    }

    @Override
    public @NotNull Collection<Block> getBlocks(Sha256Hash from) throws IOException {
        return Arrays.asList(
                new RestClient<Block[]>()
                        .get(URI.create(iNetAddress.toString() + PeerServer.BLOCKS_FROM_RESOURCE_PATH + from.toString()),
                                Block[].class));
    }

    @Override
    @Nullable
    public Block getBlock(Sha256Hash hash) throws IOException {
        try {
            return new RestClient<Block>()
                    .get(URI.create(iNetAddress.toString() + PeerServer.BLOCKS_RESOURCE_PATH + hash.toString()),
                            Block.class);
        } catch (HttpException ex) {
            if (ex.getStatus() == 404) return null;
            throw ex;
        }
    }

    @Override
    public void transmitBlock(Block block) throws IOException {
        new RestClient<Block>()
                .post(URI.create(iNetAddress.toString() + PeerServer.BLOCKS_RESOURCE_PATH), block, Block.class);
    }

    @Override
    public Collection<RemoteProxy> hello() throws IOException {
        URI iNetAddress = AppConfig.getOwnAddress(PROTOCOL);
        logger.debug("Sending own address: " + iNetAddress.toString());
        String target = this.iNetAddress.toString() + PeerServer.HELLO_HTTP_RESOURCE_PATH;
        logger.info("Helloing to target: " + target);
        return Arrays.asList(new RestClient<RemoteProxy>()
                .post(URI.create(target),
                        new HttpProxy(iNetAddress, this.ownPublicKey.toString()),
                        HttpProxy[].class));
    }
}