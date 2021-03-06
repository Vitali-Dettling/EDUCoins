package org.educoins.fullblockhainnode;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.peers.*;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.LevelDbBlockStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * The HttpServer serving {@link Block}s.
 * Manages all ingoing requests to the REST-API.
 * Created by typus on 11/5/15.
 */
@SpringBootApplication
@EnableWebMvc
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@ComponentScan(basePackages = "org.educoins.core")
@ConfigurationProperties(value = "classpath:/application.properties")
public class FullBlockChainNode {

    public static void main(String[] args) throws BlockStoreException {
        ConfigurableApplicationContext run = SpringApplication.run(FullBlockChainNode.class, args);
        IProxyPeerGroup peerGroup = new HttpProxyPeerGroup();
        FullBlockChainPeer peer = new FullBlockChainPeer(new BlockChain(peerGroup, peerGroup, peerGroup, new LevelDbBlockStore()));
        try {
            peer.start();
        } catch (DiscoveryException e) {
            SpringApplication.exit(run, () -> -1);
        }
    }
}
