package org.educoins.core.config;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.peers.HttpProxyPeerGroup;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.store.*;
import org.springframework.context.annotation.*;

import java.io.IOException;

/**
 * Configures Beans and other SpringMVC related stuff. Created by typus on
 * 11/30/15.
 */
@Configuration
@Profile("production")
public class ProductionSpringConfig {
	private IBlockStore blockStore;
	private BlockChain blockChain;
	private IProxyPeerGroup peerGroup;

	@Bean
	public IBlockStore blockStore() throws IOException {
		if (blockStore == null) {
			this.blockStore = new LevelDbBlockStore(AppConfig.getBlockStoreDirectory());
		}
		return blockStore;
	}

//	@Bean
//	public BlockChain blockChain() throws IOException {
//		if (blockChain == null) {
//			IProxyPeerGroup peerGroup = proxyPeerGroup();
//			IBlockStore store = blockStore();
//			this.blockChain = new BlockChain(peerGroup, store);
//		}
//		return blockChain;
//	}


	@Bean
	public IProxyPeerGroup proxyPeerGroup() throws BlockStoreException {
		if (peerGroup == null) {
			peerGroup = new HttpProxyPeerGroup();
		}
		return peerGroup;
	}

}
