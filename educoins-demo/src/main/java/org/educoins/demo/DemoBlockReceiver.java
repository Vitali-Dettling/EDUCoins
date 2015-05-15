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
import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.IBlockListener;
import org.educoins.core.IBlockReceiver;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class DemoBlockReceiver implements IBlockReceiver {

	private Path remoteStorage;

	private List<IBlockListener> blockListeners;

	public DemoBlockReceiver(String remoteStorage) throws IOException {
		this(Paths.get(remoteStorage));
	}

	public DemoBlockReceiver(Path remoteStorage) throws IOException {
		this.remoteStorage = remoteStorage;

		if (Files.exists(remoteStorage) && !Files.isDirectory(remoteStorage)) {
			throw new IllegalArgumentException(this.remoteStorage.toString() + " is not a directory");
		}

		if (!Files.exists(remoteStorage)) {
			Files.createDirectories(remoteStorage);
		}

		this.blockListeners = new ArrayList<>();
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
		try {
			DemoBlockReceiverThread receiverThread = new DemoBlockReceiverThread(this.remoteStorage,
					this.blockListeners);
			receiverThread.start();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	private static class DemoBlockReceiverThread extends Thread {

		private Path remoteStorage;
		private WatchService watcher;
		private WatchKey key;

		private Gson gson;

		private List<IBlockListener> blockListeners;

		public DemoBlockReceiverThread(Path remoteStorage, List<IBlockListener> blockListeners) throws IOException {
			this.remoteStorage = remoteStorage;

			this.watcher = this.remoteStorage.getFileSystem().newWatchService();
			this.key = this.remoteStorage.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

			this.gson = new Gson();

			this.blockListeners = blockListeners;
		}

		private void notifyBlockReceived(Block block) {
			for (int i = 0; i < this.blockListeners.size(); i++) {
				IBlockListener listener = this.blockListeners.get(i);
				listener.blockReceived(block);
			}
		}

		private Block parseFile(Path file) {
			try {
				FileReader reader = new FileReader(file.toFile());
				Block block = this.gson.fromJson(reader, Block.class);
				return block;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (JsonIOException e) {
				e.printStackTrace();
				return null;
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public void run() {
			while (true) {
				for (WatchEvent<?> event : this.key.pollEvents()) {
					@SuppressWarnings("unchecked")
					Path createdFile = ((WatchEvent<Path>) event).context();
					if (createdFile.toString().toLowerCase().endsWith(".json")) {
						try {
							// XXX [joeren]: Spielerischer Wert
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Block block = this.parseFile(this.remoteStorage.resolve(createdFile));
						if (block != null) {
							this.notifyBlockReceived(block);
						}
					}
				}
				key.reset();
			}
		}

	}

}
