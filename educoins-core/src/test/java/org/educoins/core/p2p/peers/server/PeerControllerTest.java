package org.educoins.core.p2p.peers.server;

import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.peers.*;
import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.InetAddress;
import java.net.URI;

import static junit.framework.TestCase.*;

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
    HttpNodeCache httpNodeCache;

    @Test
    public void testAddPeer() throws Exception {
        Peer theOtherPeer = new ReferencePeer(new HttpProxy(URI.create(HttpProxy.PROTOCOL + "localhost:8081"), "myPub1"));
        theOtherPeer.hello();

        String newINetAddr = HttpProxy.PROTOCOL + InetAddress.getLocalHost().getHostAddress()
                + ":" + AppConfig.getOwnPort();

        assertTrue(httpNodeCache.contains(new HttpProxy(
                URI.create(newINetAddr), "myPub1")));
    }


}