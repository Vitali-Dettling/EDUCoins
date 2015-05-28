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

public class DemoBlockTransmitterWallet implements IBlockTransmitter {

	private Path walletStorage;
	private Path remoteStorage;

	private Gson gson;

	public DemoBlockTransmitterWallet(String remoteStorage, String walletStorage) throws IOException {
		this(Paths.get(remoteStorage), Paths.get(walletStorage));
	}

	public DemoBlockTransmitterWallet(Path remoteStorage, Path walletStorage) throws IOException {
		this.walletStorage = walletStorage;
		this.remoteStorage = remoteStorage;

		if (Files.exists(remoteStorage) && !Files.isDirectory(remoteStorage)) {
			throw new IllegalArgumentException(this.remoteStorage.toString() + " is not a directory");
		}
		
		if (Files.exists(walletStorage) && !Files.isDirectory(walletStorage)) {
			throw new IllegalArgumentException(this.remoteStorage.toString() + " is not a directory");
		}

		if (!Files.exists(remoteStorage)) {
			Files.createDirectories(remoteStorage);
		}
		
		if (!Files.exists(walletStorage)) {
			Files.createDirectories(walletStorage);
		}

		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}

	@Override
	public void transmitBlock(Block block) {
		Path fileName = Paths.get(ByteArray.convertToString(block.hash(), 16) + ".json");
		
		Path remoteBlockFile = this.remoteStorage.resolve(fileName);
		Path walletBlockFile = this.walletStorage.resolve(fileName);
		
		try {
			
			Thread.sleep(10);
			Files.copy(remoteBlockFile, walletBlockFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
