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
    private final Logger logger = LoggerFactory.getLogger(CentralDiscovery.class);
    private String centralUrl;

    public CentralDiscovery() {
        this(AppConfig.getCentralUrl());
    }

    public CentralDiscovery(String centralUrl) {
        this.centralUrl = centralUrl;
    }

    @Override
    public void hello() throws DiscoveryException {
        try {
            String pubkey = AppConfig.getOwnPublicKey().toString();
            logger.info("Helloing Central with Public Key {} now...", pubkey);
            HttpProxy thisProxy = new HttpProxy(URI.create("localhost"), pubkey);

            thisProxy.setPort(AppConfig.getOwnPort());

            String ip = new RestClient<RemoteProxy>()
                    .post(URI.create(centralUrl + NODES), thisProxy, String.class);

            AppConfig.setInetAddress(ip);

            logger.info("Helloing Central successful. Retrieved new IP: {}", ip);
        } catch (Exception e) {
            throw new DiscoveryException(e);
        }

    }

    @Override
    public @NotNull Collection<RemoteProxy> getPeers() throws DiscoveryException {
        logger.info("Requesting ReferenceNodes...");
        return getRemoteProxies(NODES);
    }

    @NotNull
    private Collection<RemoteProxy> getRemoteProxies(String uri) throws DiscoveryException {
        try {
            List<RemoteProxy> peers = new ArrayList<>();
            RemoteProxy[] nodes = new RestClient<RemoteProxy[]>()
                    .get(URI.create(centralUrl + uri), HttpProxy[].class);

            if (nodes == null) throw new DiscoveryException("No nodes received.");
            return Arrays.asList(nodes);

        } catch (IOException e) {
            throw new DiscoveryException(e);
        }
    }
}
