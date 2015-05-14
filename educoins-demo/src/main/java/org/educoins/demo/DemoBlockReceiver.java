package org.educoins.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.IBlockListener;
import org.educoins.core.IBlockReceiver;

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
		// TODO Auto-generated method stub

	}

}
