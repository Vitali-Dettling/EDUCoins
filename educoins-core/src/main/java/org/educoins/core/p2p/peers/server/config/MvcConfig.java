package org.educoins.core.p2p.peers.server.config;

import org.educoins.core.*;
import org.educoins.core.p2p.peers.HttpProxyPeerGroup;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.p2p.peers.LocalPeer;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.store.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Beans and other SpringMVC related stuff.
 * Created by typus on 11/30/15.
 */
@Configuration
public class MvcConfig {
    private IBlockStore blockStore;
    private BlockChain blockChain;
    private IProxyPeerGroup peerGroup;
    private Miner miner;

    @Bean
    public IBlockStore blockStore() throws BlockStoreException {
        if (blockStore == null) {
            this.blockStore = new LevelDbBlockStore();
        }
        return blockStore;
    }

    @NotNull
    private Block generateBlock() {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        return toReturn;
    }

    //TODO: think of typing and so on.
    @Bean
    public BlockChain blockChain() throws BlockStoreException {
        if (blockChain == null) {
        	IProxyPeerGroup proxyPeerGroup = proxyPeerGroup();
        	IBlockStore store = blockStore();
        	Miner miner = miner();
    		
            this.blockChain = new BlockChain(proxyPeerGroup, miner, proxyPeerGroup, proxyPeerGroup, store);
            this.blockChain.setMiner(miner);
        }
        return blockChain;
    }
    
    @Bean
    public Miner miner(){
    	 if (this.miner == null) {
    		 this.miner = new Miner();
    	 }
    	 return this.miner;
    }

    @Bean
    public IProxyPeerGroup proxyPeerGroup() throws BlockStoreException {
        if (peerGroup == null) {
            peerGroup = new HttpProxyPeerGroup();
        }
        return peerGroup;
    }

}


