package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.*;
import org.educoins.core.utils.RestClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The {@link DiscoveryStrategy} using the <code>educoins-central</code>-Webservice to find appropriate {@link Peer}s.
 * Created by typus on 11/3/15.
 */
public class CentralDiscovery implements DiscoveryStrategy {

    private String centralUrl;

    public CentralDiscovery(String centralUrl) {
        this.centralUrl = centralUrl;
    }

    @Override
    public Collection<Peer> getFullPeers() throws DiscoveryException {
        try {
            List<Peer> peers = new ArrayList<>();
            RemoteNode[] nodes = new RestClient<RemoteNode[]>()
                    .get(new URI(centralUrl + "/nodes/full"), HttpNode[].class);

            for (RemoteNode node : nodes) {
                peers.add(new FullPeer(node));
            }

            return peers;
        } catch (IOException | URISyntaxException e) {
            throw new DiscoveryException(e);
        }
    }

    @Override
    public Collection<Peer> getReadOnlyPeers() throws DiscoveryException {
        try {
            List<Peer> peers = new ArrayList<>();
            RemoteNode[] nodes = new RestClient<RemoteNode[]>()
                    .get(new URI(centralUrl + "/nodes/read-only"), HttpNode[].class);

            for (RemoteNode node : nodes) {
                peers.add(new ReadOnlyPeer(node));
            }

            return peers;
        } catch (IOException | URISyntaxException e) {
            throw new DiscoveryException(e);
        }
    }

}
