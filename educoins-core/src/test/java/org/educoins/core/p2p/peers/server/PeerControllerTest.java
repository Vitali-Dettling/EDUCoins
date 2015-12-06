package org.educoins.core.p2p.peers.server;

import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.peers.HttpProxyPeerGroup;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.InetAddress;
import java.net.URI;
import java.util.Collection;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Tests {@link PeerController#addHttpPeer(HttpProxy)}
 * Created by typus on 12/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PeerServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8081")
public class PeerControllerTest {

    @Autowired
    private HttpProxyPeerGroup peerGroup;

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Test
    public void testAddPeer() throws Exception {
        String newINetAddr = HttpProxy.PROTOCOL + InetAddress.getLocalHost().getHostAddress()
                + ":" + AppConfig.getOwnPort();
        HttpProxy proxy = new HttpProxy(URI.create(newINetAddr), AppConfig.getOwnPublicKey().toString());

        HttpProxy foreignNode = new HttpProxy(URI.create(HttpProxy.PROTOCOL + "localhost:8081"), "myPub1");
        foreignNode.hello();

        //check logs -> shows that the controller received the correct node.
        //(But PeerGroup did not add it because its the same pubkey as the receiver.)
        assertThat(capture.toString(), containsString("Retrieved peer RemoteProxy{iNetAddress=http://"));
        assertThat(capture.toString(), containsString("pubkey='" + AppConfig.getOwnPublicKey().toString() + "'"));

        //Having same PubKey so must not be added.
        assertFalse(peerGroup.containsProxy(proxy));
    }

}