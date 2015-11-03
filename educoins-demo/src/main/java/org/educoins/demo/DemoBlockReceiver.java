package org.educoins.demo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.educoins.core.Block;
import org.educoins.core.IBlockListener;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.p2p.nodes.Peer;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Threading;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sun.istack.internal.NotNull;

public class DemoBlockReceiver implements IBlockReceiver {

	private IBlockStore blockStore;

	private final Set<IBlockListener> blockListeners = new HashSet<>();

	public DemoBlockReceiver(@NotNull IBlockStore blockStore) throws IOException {
		this.blockStore = blockStore;
	}

//	public DemoBlockReceiver(Path localDBStorage) throws IOException {
//		this.blockListeners = new ArrayList<>();
//	}

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
		//try {	
			
			Block latestBlock = this.blockStore.getLatest();

            Threading.run(() -> 
            blockListeners.forEach(iBlockListener -> iBlockListener.blockReceived(latestBlock)));
	        
	        
//			DemoBlockReceiverThread receiverThread = new DemoBlockReceiverThread(this.blockStore,
//					this.blockListeners);
//			receiverThread.start();
//		} catch (IOException e) {
//			System.err.println(e.getMessage());
//			System.exit(-1);
//		}
	}

	private static class DemoBlockReceiverThread extends Thread {

//		private Path localDBStorage;
//		private WatchService watcher;
//		private WatchKey key;
//		private Gson gson;
		private List<IBlockListener> blockListeners;
		private IBlockStore blockStore;

		public DemoBlockReceiverThread(@NotNull IBlockStore blockStore, Set<IBlockListener> blockListeners2) throws IOException {
			this.setName("DemoBlockReceiverThread");
//			this.localDBStorage = localDBStorage;
//			this.watcher = this.localDBStorage.getFileSystem().newWatchService();
//			this.key = this.localDBStorage.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
//			this.gson = new Gson();
			this.blockStore = blockStore;
			this.blockListeners = blockListeners;
		}

		private void notifyBlockReceived(Block block) {
			for (int i = 0; i < this.blockListeners.size(); i++) {
				IBlockListener listener = this.blockListeners.get(i);
				listener.blockReceived(block);
			}
		}

//		private Block parseFile(Path file) {
//			try {
//				FileReader reader = new FileReader(file.toFile());
//				Block block = this.gson.fromJson(reader, Block.class);
//				return block;
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				return null;
//			} catch (JsonIOException e) {
//				e.printStackTrace();
//				return null;
//			} catch (JsonSyntaxException e) {
//				e.printStackTrace();
//				return null;
//			}
//		}

		@SuppressWarnings("null")
		@Override
		public void run() {
			
			Block current = null;
			while (true) {
				Block latestBlock = this.blockStore.getLatest();
				if(latestBlock != null && (current != null ||current.getHashPrevBlock() != latestBlock.getHashPrevBlock())){
		            Threading.run(() -> 
		            blockListeners.forEach(iBlockListener -> iBlockListener.blockReceived(latestBlock)));
		            current = latestBlock;
				}
			
//				Block latestBlock = this.blockStore.getLatest();
//				if(latestBlock != null){
//					this.notifyBlockReceived(latestBlock);
//				}
			}
			
//			while (true) {
//				
////				for (WatchEvent<?> event : this.key.pollEvents()) {
////					
//////					@SuppressWarnings("unchecked")
//////					Path createdFile = ((WatchEvent<Path>) event).context();
//////					if (createdFile.toString().toLowerCase().endsWith(".json")) {
//////						try {
//////							// XXX [joeren]: Spielerischer Wert
//////							Thread.sleep(10);
//////						} catch (InterruptedException e) {
//////							e.printStackTrace();
//////						}
////						
////						
////						
////						//Block block = this.parseFile(this.localDBStorage.resolve(createdFile));
////						if (block != null) {
////							this.notifyBlockReceived(block);
////						}
////					}
//				
//				
////				}
//				//key.reset();
//			}
			
			
			
		}

	}

}
