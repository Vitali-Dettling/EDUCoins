package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.*;
import org.educoins.core.p2p.peers.remote.HttpNode;
import org.educoins.core.p2p.peers.remote.RemoteNode;
import org.educoins.core.testutils.FieldInjector;
import org.educoins.core.utils.RestClient;
import org.junit.*;
import org.junit.rules.ExpectedException;
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

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    String centralUrl = "http://localhost:8080/";
    DiscoveryStrategy discovery = new CentralDiscovery(centralUrl);
    RestClient clientMock = Mockito.mock(RestClient.class);
    List<Peer> expectedPeers = new ArrayList<>();
    private URI UriFull;
    private URI UriReadOnly;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException, IOException, URISyntaxException {
        UriFull = new URI(centralUrl + "/nodes/full");
        UriReadOnly = new URI(centralUrl + "/nodes/read-only");
        FieldInjector.setField(discovery, clientMock, "client");
    }

    @Test
    public void testErrors() throws URISyntaxException, IOException {
        when(clientMock.get(UriFull, HttpNode[].class)).thenReturn(null);
        assertNotNull(discovery.getFullPeers());

        when(clientMock.get(UriReadOnly, HttpNode[].class)).thenReturn(null);
        assertNotNull(discovery.getReadOnlyPeers());

        when(clientMock.get(UriReadOnly, HttpNode[].class)).thenThrow(new IOException("ERROR"));
        exception.expect(DiscoveryException.class);
        discovery.getReadOnlyPeers();

        when(clientMock.get(UriFull, HttpNode[].class)).thenThrow(new IOException("ERROR"));
        exception.expect(DiscoveryException.class);
        discovery.getFullPeers();
    }

    @Test
    public void testGetFullPeers() throws Exception {
        RemoteNode[] remoteNodes = new RemoteNode[]{new HttpNode()};
        for (RemoteNode node : remoteNodes) {
            expectedPeers.add(new FullPeer(node));
        }
        when(clientMock.get(UriFull, HttpNode[].class)).thenReturn(remoteNodes);

        Collection<Peer> fullPeersActual = discovery.getFullPeers();
        fullPeersActual.forEach(peer -> assertTrue(expectedPeers.contains(peer)));
    }

    @Test
    public void testGetReadOnlyPeers() throws Exception {
        RemoteNode[] remoteNodes = new RemoteNode[]{new HttpNode()};
        for (RemoteNode node : remoteNodes) {
            expectedPeers.add(new ReadOnlyPeer(node));
        }
        when(clientMock.get(UriReadOnly, HttpNode[].class)).thenReturn(remoteNodes);

        Collection<Peer> readOnlyPeersActual = discovery.getReadOnlyPeers();
        readOnlyPeersActual.forEach(peer -> assertTrue(expectedPeers.contains(peer)));
    }
}