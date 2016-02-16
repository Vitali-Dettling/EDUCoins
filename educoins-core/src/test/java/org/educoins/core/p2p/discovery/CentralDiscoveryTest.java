package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.educoins.core.testutils.FieldInjector;
import org.educoins.core.utils.AppConfigInitializer;
import org.educoins.core.utils.RestClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests {@link CentralDiscovery}.
 * Created by typus on 11/12/15.
 */
public class CentralDiscoveryTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    String centralUrl = "http://localhost:1337";
    DiscoveryStrategy discovery;
    RestClient clientMock = Mockito.mock(RestClient.class);
    List<RemoteProxy> expectedPeers = new ArrayList<>();
    private URI uriReference;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException, IOException, URISyntaxException {
        AppConfigInitializer.init();
        uriReference = new URI(centralUrl + "/nodes/");
        discovery = new CentralDiscovery();
        FieldInjector.setField(discovery, clientMock, "client");
    }

    @Test
    @Ignore
    public void testErrors() throws URISyntaxException, IOException {
        when(clientMock.get(uriReference, HttpProxy[].class)).thenReturn(null);
        assertNotNull(discovery.getPeers());

        when(clientMock.get(uriReference, HttpProxy[].class)).thenThrow(new IOException("ERROR"));
        exception.expect(DiscoveryException.class);
        discovery.getPeers();
    }

    @Test
    public void testGetFullPeers() throws Exception {
        RemoteProxy[] remoteProxies = new RemoteProxy[]{new HttpProxy()};
        Collections.addAll(expectedPeers, remoteProxies);
        when(clientMock.get(uriReference, HttpProxy[].class)).thenReturn(remoteProxies);

        Collection<RemoteProxy> fullPeersActual = discovery.getPeers();
        fullPeersActual.forEach(peer -> assertTrue(expectedPeers.contains(peer)));
    }
}