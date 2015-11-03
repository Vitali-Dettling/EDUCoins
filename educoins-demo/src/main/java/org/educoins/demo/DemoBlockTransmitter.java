package org.educoins.demo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.educoins.core.Block;
import org.educoins.core.IBlockTransmitter;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.core.utils.ByteArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DemoBlockTransmitter implements IBlockTransmitter {

    private IBlockStore store;
	private Path localStorage;
	private Gson gson;

	public DemoBlockTransmitter(IBlockStore localDBStorage) throws IOException {
		
		this.store = localDBStorage;
	}

	public DemoBlockTransmitter(Path localStorage, Path remoteStorage) throws IOException {
		this.localStorage = localStorage;

		if (Files.exists(localStorage) && !Files.isDirectory(localStorage)) {
			throw new IllegalArgumentException(this.localStorage.toString() + " is not a directory");
		}

		if (!Files.exists(localStorage)) {
			Files.createDirectories(localStorage);
		}

		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}

	@Override
	public void transmitBlock(Block block) {

	}

}
