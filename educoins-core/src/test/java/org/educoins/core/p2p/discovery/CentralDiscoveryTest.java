package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.*;
import org.educoins.core.p2p.peers.remote.HttpNode;
import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.educoins.core.testutils.FieldInjector;
import org.educoins.core.utils.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link CentralDiscovery}.
 * Created by typus on 11/12/15.
 */
public class CentralDiscoveryTest {

    String centralUrl = "http://localhost:8080/";
    DiscoveryStrategy discovery = new CentralDiscovery(centralUrl);
    RestClient clientMock = Mockito.mock(RestClient.class);
    List<Peer> expectedPeers = new ArrayList<>();

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException, IOException, URISyntaxException {


        FieldInjector.setField(discovery, clientMock, "client");
    }

    @Test
    public void testGetFullPeers() throws Exception {
        RemoteNode[] remoteNodes = new RemoteNode[]{new HttpNode()};
        for (RemoteNode node : remoteNodes) {
            expectedPeers.add(new FullPeer(node));
        }
        when(clientMock.get(new URI(centralUrl + "/nodes/full"), HttpNode[].class)).thenReturn(remoteNodes);

        Collection<Peer> fullPeersActual = discovery.getFullPeers();
        fullPeersActual.forEach(peer -> assertTrue(expectedPeers.contains(peer)));
    }

    @Test
    public void testGetReadOnlyPeers() throws Exception {
        RemoteNode[] remoteNodes = new RemoteNode[]{new HttpNode()};
        for (RemoteNode node : remoteNodes) {
            expectedPeers.add(new ReadOnlyPeer(node));
        }
        when(clientMock.get(new URI(centralUrl + "/nodes/read-only"), HttpNode[].class)).thenReturn(remoteNodes);

        Collection<Peer> readOnlyPeersActual = discovery.getReadOnlyPeers();
        readOnlyPeersActual.forEach(peer -> assertTrue(expectedPeers.contains(peer)));
    }
}