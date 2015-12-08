package org.educoins.core.p2p.discovery;

import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.educoins.core.utils.RestClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * The {@link DiscoveryStrategy} using the <code>educoins-central</code>-Webservice to find appropriate {@link Peer}s.
 * Created by typus on 11/3/15.
 */
public class CentralDiscovery implements DiscoveryStrategy {
    public static final String NODES = "nodes/";
    public static final String RESOURCE_NODES_MINER = "nodes/miner";
    public static final String RESOURCE_NODES_BLOCKCHAIN = "nodes/blockchain";
    public static final String RESOURCE_NODES_REFERENCE = "nodes/reference";
    private final Logger logger = LoggerFactory.getLogger(CentralDiscovery.class);
    private String centralUrl;
    private RestClient<RemoteProxy[]> client;

    public CentralDiscovery() {
        this.centralUrl = AppConfig.getCentralUrl();
        this.client = new RestClient<>();
    }

    @Override
    public void hello() throws DiscoveryException {
        try {
            new RestClient<RemoteProxy>()
                    .post(URI.create(centralUrl + NODES),
                            new HttpProxy(AppConfig.getOwnAddress("http://"),
                                    AppConfig.getOwnPublicKey().toString()));
        } catch (Exception e) {
            throw new DiscoveryException(e);
        }

    }

    @Override
    public @NotNull Collection<RemoteProxy> getReferencePeers() throws DiscoveryException {
        logger.info("Requesting ReferenceNodes...");
        return getRemoteProxies(RESOURCE_NODES_REFERENCE);
    }

    @Override
    public @NotNull Collection<RemoteProxy> getFullBlockchainPeers() throws DiscoveryException {
        logger.info("Requesting FullBlockChainNodes...");
        return getRemoteProxies(RESOURCE_NODES_BLOCKCHAIN);
    }

    @Override
    public @NotNull Collection<RemoteProxy> getSoloMinerPeers() throws DiscoveryException {
        logger.info("Requesting SoloMinerNodes...");
        return getRemoteProxies(RESOURCE_NODES_MINER);
    }

    @NotNull
    private Collection<RemoteProxy> getRemoteProxies(String uri) throws DiscoveryException {
        try {
            List<RemoteProxy> peers = new ArrayList<>();
            RemoteProxy[] nodes = client
                    .get(URI.create(centralUrl + uri), HttpProxy[].class);

            if (nodes == null) return peers;
            return Arrays.asList(nodes);

        } catch (IOException e) {
            throw new DiscoveryException(e);
        }
    }
}
