package org.educoins.core.utils;

import org.educoins.core.p2p.discovery.TopTenProxySelector;
import org.educoins.core.p2p.peers.HttpProxyPeerGroup;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.store.*;
import org.springframework.context.annotation.*;

import java.io.IOException;

/**
 * The Spring configuration for testing. This is predominantly necessary to have no {@link LevelDbBlockStore} clashes.
 */
@Configuration
@Profile("test")
@DependsOn("appConfig")
public class TestSpringConfig {
    private IBlockStore blockStore;
    private IProxyPeerGroup peerGroup;

    @Bean
    public IBlockStore blockStore() throws IOException {
        if (blockStore == null) {
            this.blockStore = new LevelDbBlockStore(IO.getRandomizedBlockStoreFile());
        }
        return blockStore;
    }

    @Bean
    public IProxyPeerGroup proxyPeerGroup() throws BlockStoreException {
        if (peerGroup == null) {
            peerGroup = new HttpProxyPeerGroup(new TopTenProxySelector());
        }
        return peerGroup;
    }
}