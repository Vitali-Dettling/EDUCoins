package org.educoins.core.p2p.peers.server;

import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.peers.HttpProxyPeerGroup;
import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static org.hamcrest.core.StringContains.*;
import static org.junit.Assert.*;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Tests {@link PeerController#addHttpPeer(HttpProxy, HttpServletRequest)}
 * Created by typus on 12/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PeerServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8081")
@ActiveProfiles("test")
public class PeerControllerTest {

    @Rule
    public OutputCapture capture = new OutputCapture();
    @Autowired
    private HttpProxyPeerGroup peerGroup;

    @SuppressWarnings("deprecation")
	@Test
    public void testAddPeer() throws Exception {
        AppConfig.setInetAddress("localhost");
        HttpProxy proxy = new HttpProxy(AppConfig.getOwnAddress(HttpProxy.PROTOCOL), AppConfig.getOwnPublicKey().toString());

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