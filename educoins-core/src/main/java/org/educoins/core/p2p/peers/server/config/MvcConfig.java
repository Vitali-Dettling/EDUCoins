package org.educoins.core.p2p.peers.server.config;

import org.educoins.core.*;
import org.educoins.core.p2p.peers.LocalPeer;
import org.educoins.core.store.*;
import org.jetbrains.annotations.NotNull;
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

    //TODO: remove block generation
    public Block getRandomBlock(Block block) {
        Block toReturn = getRandomBlock();
        toReturn.setHashPrevBlock(block.hash());
        return toReturn;
    }

    public Block getRandomBlock() {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        for (int i = 0; i < Math.random() * 1000; ++i)
            toReturn.addTransaction(new Transaction());
        return toReturn;
    }

    @Bean
    public IBlockStore blockStore() throws BlockStoreException {
        if (blockStore == null) {
            this.blockStore = new LevelDbBlockStore();
            fillRandomTree(this.blockStore);
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
            LocalPeer localPeer = new LocalPeer();
            this.blockChain = new BlockChain(localPeer, localPeer, localPeer, blockStore());
        }
        return blockChain;
    }

    public void fillRandomTree(IBlockStore store) {
        Block previous = getRandomBlock();
        for (int i = 0; i < 23; i++) {
            previous = getRandomBlock(previous);
            store.put(previous);
        }
    }
}


