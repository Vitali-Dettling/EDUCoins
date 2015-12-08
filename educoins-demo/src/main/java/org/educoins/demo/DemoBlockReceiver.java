package org.educoins.demo;

import org.educoins.core.*;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.Sha256Hash;
import org.educoins.core.utils.Threading;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DemoBlockReceiver implements IBlockReceiver {

    private final Set<IBlockListener> blockListeners = new HashSet<>();
    private IBlockStore blockStore;

    public DemoBlockReceiver(@NotNull IBlockStore blockStore) throws IOException {
        this.blockStore = blockStore;
    }

    @Override
    public void addBlockListener(IBlockListener blockListener) {
        this.blockListeners.add(blockListener);
    }

    @Override
    public void removeBlockListener(IBlockListener blockListener) {
        this.blockListeners.remove(blockListener);
    }

    @Override
    public void receiveBlocks(Sha256Hash from) {
        receiveBlocks();
    }

    public void receiveBlocks() {

        Block latestBlock = this.blockStore.getLatest();
        Threading.run(() ->
                blockListeners.forEach(iBlockListener -> iBlockListener.blockReceived(latestBlock)));
    }
}
