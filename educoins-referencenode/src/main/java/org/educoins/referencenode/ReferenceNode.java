package org.educoins.referencenode;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.peers.HttpProxyPeerGroup;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.p2p.peers.ReferencePeer;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.LevelDbBlockStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
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
public class ReferenceNode {

    public static void main(String[] args) throws BlockStoreException {
        ConfigurableApplicationContext run = SpringApplication.run(ReferenceNode.class, args);
        IProxyPeerGroup peerGroup = new HttpProxyPeerGroup();
        BlockChain blockChain = new BlockChain(peerGroup, peerGroup, peerGroup, peerGroup, new LevelDbBlockStore());
        ReferencePeer peer = new ReferencePeer(blockChain, new Wallet());

        //TODO: for demo, remove afterwards
        try {
            peer.start();
        } catch (DiscoveryException e) {
            SpringApplication.exit(run, () -> -1);
        }


//        ConfigurableApplicationContext run = SpringApplication.run(ReferenceNode.class, args);
//        IProxyPeerGroup peerGroup = new HttpProxyPeerGroup();
//        BlockChain blockChain = new BlockChain(peerGroup, peerGroup, peerGroup, new LevelDbBlockStore());
//        Miner miner = new Miner(blockChain);
//        ReferencePeer peer = new ReferencePeer(blockChain, miner, new Wallet());
//        try {
//            peer.start();
//        } catch (DiscoveryException e) {
//            SpringApplication.exit(run, () -> -1);
//        }
    }
}
