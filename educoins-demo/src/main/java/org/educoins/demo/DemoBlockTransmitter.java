package org.educoins.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.educoins.core.Block;
import org.educoins.core.IBlockTransmitter;

public class DemoBlockTransmitter implements IBlockTransmitter {

	private Path localStorage;
	private Path remoteStorage;

	public DemoBlockTransmitter(String localStorage, String remoteStorage) throws IOException {
		this(Paths.get(localStorage), Paths.get(remoteStorage));
	}

	public DemoBlockTransmitter(Path localStorage, Path remoteStorage) throws IOException {
		this.localStorage = localStorage;
		this.remoteStorage = remoteStorage;

		if (Files.exists(localStorage) && !Files.isDirectory(localStorage)) {
			throw new IllegalArgumentException(this.localStorage.toString() + " is not a directory");
		}

		if (Files.exists(remoteStorage) && !Files.isDirectory(remoteStorage)) {
			throw new IllegalArgumentException(this.remoteStorage.toString() + " is not a directory");
		}

		if (!Files.exists(localStorage)) {
			Files.createDirectories(localStorage);
		}

		if (!Files.exists(remoteStorage)) {
			Files.createDirectories(remoteStorage);
		}
	}

	@Override
	public void transmitBlock(Block block) {
		
	}

}
