package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.*;
import org.educoins.core.p2p.peers.remote.HttpNode;
import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.educoins.core.utils.RestClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * The {@link DiscoveryStrategy} using the <code>educoins-central</code>-Webservice to find appropriate {@link Peer}s.
 * Created by typus on 11/3/15.
 */
public class CentralDiscovery implements DiscoveryStrategy {

    public static final String RESOURCE_NODES_MINER = "/nodes/miner";
    public static final String RESOURCE_NODES_BLOCKCHAIN = "/nodes/blockchain";
    public static final String RESOURCE_NODES_REFERENCE = "nodes/reference";
    private String centralUrl;
    private RestClient<RemoteNode[]> client;

    public CentralDiscovery(@NotNull String centralUrl) {
        this.centralUrl = centralUrl;
    }

    @Override
    public @NotNull Collection<Peer> getReferencePeers() throws DiscoveryException {
        try {
            List<Peer> peers = new ArrayList<>();
            RemoteNode[] nodes = client
                    .get(new URI(centralUrl + RESOURCE_NODES_REFERENCE), HttpNode[].class);

            if (nodes == null) return peers;

            for (RemoteNode node : nodes) {
                peers.add(new ReferencePeer(node));
            }

            return peers;
        } catch (IOException | URISyntaxException e) {
            throw new DiscoveryException(e);
        }
    }

    @Override
    public @NotNull Collection<Peer> getFullBlockchainPeers() throws DiscoveryException {
        try {
            List<Peer> peers = new ArrayList<>();
            RemoteNode[] nodes = client
                    .get(new URI(centralUrl + RESOURCE_NODES_BLOCKCHAIN), HttpNode[].class);

            if (nodes == null) return peers;

            for (RemoteNode node : nodes) {
                peers.add(new FullBlockChainPeer(node));
            }

            return peers;
        } catch (IOException | URISyntaxException e) {
            throw new DiscoveryException(e);
        }
    }

    @Override
    public @NotNull Collection<Peer> getSoloMinerPeers() throws DiscoveryException {
        try {
            List<Peer> peers = new ArrayList<>();
            RemoteNode[] nodes = client
                    .get(new URI(centralUrl + RESOURCE_NODES_MINER), HttpNode[].class);

            if (nodes == null) return peers;

            for (RemoteNode node : nodes) {
                peers.add(new SoloMinerPeer(node));
            }

            return peers;
        } catch (IOException | URISyntaxException e) {
            throw new DiscoveryException(e);
        }
    }
}
