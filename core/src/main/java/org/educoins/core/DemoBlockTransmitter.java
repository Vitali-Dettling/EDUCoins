package org.educoins.core;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.educoins.core.miner.Block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DemoBlockTransmitter implements IBlockTransmitter {

	private String blockFolderPath;

	private FilenameFilter jsonFilter;
	private Gson blockFileCreator;

	public DemoBlockTransmitter(String blockFolderPath) {
		// if the parameter is null
		if (blockFolderPath == null) {
			throw new IllegalArgumentException("parameter \"blockFolderPath\" must not be null");
		}

		File folder = new File(blockFolderPath);

		// if the parameter points to a *normal* file
		if (folder.exists() && folder.isFile()) {
			throw new IllegalArgumentException("parameter \"blockFolderPath\" must be the path of a folder");
		}

		// if the parameter points to a forbidden folder
		if (folder.exists() && !folder.canWrite()) {
			throw new IllegalArgumentException("no permissions on folder " + blockFolderPath);
		}

		// create the folder
		if (!folder.exists()) {
			try {
				folder.mkdirs();
			} catch (SecurityException ex) {
				// if the folder can not be created
				throw new IllegalArgumentException("no permissions to create folder " + blockFolderPath);
			}
		}

		// assign the parameter to the member
		this.blockFolderPath = blockFolderPath;

		// create a filter for JSON files, needed by the
		// getNumberOfExistingBlocks method
		this.jsonFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith(".json")) {
					return true;
				} else {
					return false;
				}
			}
		};

		this.blockFileCreator = new GsonBuilder().setPrettyPrinting().create();
	}

	private int getNumberOfExistingBlocks() {
		File folder = new File(this.blockFolderPath);
		File[] blocks = folder.listFiles(this.jsonFilter);
		return blocks.length;
	}

	@Override
	public void sendBlock(Block block) {
		int fileNumber = this.getNumberOfExistingBlocks();
		File file = new File(this.blockFolderPath, String.format("block_%03d.json", fileNumber));
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file, false);
			String jsonString = this.blockFileCreator.toJson(block);	
			fileWriter.write(jsonString);
		} catch (IOException e) {
			System.err.println("error while sending block");
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

}
