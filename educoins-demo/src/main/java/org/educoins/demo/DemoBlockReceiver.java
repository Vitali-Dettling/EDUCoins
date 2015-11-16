package org.educoins.demo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.educoins.core.Block;
import org.educoins.core.IBlockListener;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.Threading;

import com.sun.istack.internal.NotNull;

@SuppressWarnings("restriction")
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
            this.blockListeners.forEach((iBlockListener) -> iBlockListener.blockReceived(latestBlock)));
            
	}	
}
