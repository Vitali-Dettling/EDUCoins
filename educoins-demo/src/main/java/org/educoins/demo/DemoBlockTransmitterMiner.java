package org.educoins.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.educoins.core.Block;
import org.educoins.core.IBlockTransmitter;
import org.educoins.core.utils.ByteArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DemoBlockTransmitterMiner implements IBlockTransmitter {

	private Path localStorage;
	private Path remoteStorage;

	private Gson gson;

	public DemoBlockTransmitterMiner(String localStorage, String remoteStorage) throws IOException {
		this(Paths.get(localStorage), Paths.get(remoteStorage));
	}

	public DemoBlockTransmitterMiner(Path localStorage, Path remoteStorage) throws IOException {
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

		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}

	@Override
	public void transmitBlock(Block block) {
		Path fileName = Paths.get(ByteArray.convertToString(block.hash(), 16) + ".json");
		
		Path localBlockFile = this.localStorage.resolve(fileName);
		Path remoteBlockFile = this.remoteStorage.resolve(fileName);
		try {
			Files.createFile(localBlockFile);
			String json = this.gson.toJson(block);
			FileWriter writer = new FileWriter(localBlockFile.toFile());
			writer.write(json);
			writer.close();
			Thread.sleep(5);
			Files.copy(localBlockFile, remoteBlockFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
