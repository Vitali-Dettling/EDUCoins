package org.educoins.demo;

import org.jetbrains.annotations.NotNull;
import org.educoins.core.Block;
import org.educoins.core.IBlockListener;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.Threading;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DemoBlockReceiver implements IBlockReceiver {

	private IBlockStore blockStore;

	private final Set<IBlockListener> blockListeners = new HashSet<>();

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
	public void receiveBlocks() {
			
			Block latestBlock = this.blockStore.getLatest();
            Threading.run(() -> 
            blockListeners.forEach(iBlockListener -> iBlockListener.blockReceived(latestBlock)));
	}	
}
