package org.educoins.core.p2p.peers.server.config;

import org.educoins.core.store.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Beans and other SpringMVC related stuff.
 * Created by typus on 11/30/15.
 */
@Configuration
public class MvcConfig {
    private IBlockStore blockStore;

    @Bean
    public IBlockStore blockStore() throws BlockStoreException {
        if (blockStore == null)
            this.blockStore = new LevelDbBlockStore();
        return blockStore;
    }
}


