package org.educoins.miner;

import org.educoins.core.BlockChain;
import org.educoins.core.Miner;
import org.educoins.core.Wallet;
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

@SpringBootApplication
@EnableWebMvc
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@ComponentScan(basePackages = "org.educoins.core")
//@ConfigurationProperties(value = "classpath:/application.properties")
public class SoloMinerNode {

    public static void main(String[] args) throws BlockStoreException {
    	 ConfigurableApplicationContext run = SpringApplication.run(SoloMinerNode.class, args);
         BlockChain blockChain = (BlockChain) run.getBean("blockChain");
        
         SoloMinerPeer peer = new SoloMinerPeer(blockChain);

         try {
             peer.start();
         } catch (DiscoveryException e) {
             SpringApplication.exit(run, () -> -1);
         }
    }
}