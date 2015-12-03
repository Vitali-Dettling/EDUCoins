package org.educoins.referencenode;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.peers.*;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.LevelDbBlockStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;

/**
 * The HttpServer serving {@link Block}s.
 * Manages all ingoing requests to the REST-API.
 * Created by typus on 11/5/15.
 */
@SpringBootApplication
@EnableWebMvc
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@ComponentScan(basePackages = "org.educoins.core")
public class ReferenceNode {

    private ReferencePeer peer;

    public static void main(String[] args) {
        SpringApplication.run(ReferenceNode.class, args);
    }

    @PostConstruct
    private void start() throws BlockStoreException, DiscoveryException {
        IProxyPeerGroup peerGroup = new HttpProxyPeerGroup();
        peer = new ReferencePeer(new BlockChain(peerGroup, peerGroup, peerGroup, new LevelDbBlockStore()), new Wallet());
        peer.start();
    }
}
