package org.educoins.referencenode;

import org.educoins.core.*;
import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.p2p.peers.ReferencePeer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;

/**
 * The HttpServer serving {@link Block}s.
 * Manages all ingoing requests to the REST-API.
 * Created by typus on 11/5/15.
 */
@SpringBootApplication
@EnableWebMvc
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@ComponentScan(basePackages = "org.educoins.core")
@EnableScheduling
public class ReferenceNode {

    public static void main(String[] args) throws IOException {
        Wallet.initialize(args);

        ConfigurableApplicationContext run = SpringApplication.run(ReferenceNode.class, args);
        BlockChain blockChain = (BlockChain) run.getBean("blockChain");
        IProxyPeerGroup peerGroup = (IProxyPeerGroup) run.getBean("proxyPeerGroup");
        ReferencePeer peer = new ReferencePeer(blockChain, peerGroup, AppConfig.getOwnPublicKey());

        try {
            peer.start();
        } catch (DiscoveryException e) {
            SpringApplication.exit(run, () -> -1);
        }
    }
}
