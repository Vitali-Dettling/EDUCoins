package org.educoins.core.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.educoins.core.Block;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public abstract class Deserializer {

	private static final Gson GSON_DESERIALIZER = new Gson();

	public static Block deserialize(String blockChainPath, String fileHashName) throws FileNotFoundException,
			JsonIOException, JsonSyntaxException {

		Path remoteStorage = Paths.get(blockChainPath);

		if (Files.exists(remoteStorage) && !Files.isDirectory(remoteStorage)) {
			throw new IllegalArgumentException(remoteStorage.toString() + " is not a directory");
		}

		Path fileName = Paths.get(fileHashName + ".json");
		Path remoteBlockFile = remoteStorage.resolve(fileName);

		FileReader reader = new FileReader(remoteBlockFile.toFile());
		Gson gson = new Gson();
		return gson.fromJson(reader, Block.class);

	}

	public static Block deserialize(String jsonString) {
		try {
			Block block = GSON_DESERIALIZER.fromJson(jsonString, Block.class);
			return block;
		} catch (Exception ex) {
			System.out.println("Deserializer.deserialize: Error while deserialize json String");
			return null;
		}
	}

}
