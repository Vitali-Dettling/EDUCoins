package org.educoins.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.miner.Block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DemoBlockReceiver implements IBlockReceiver {

	private String blockFolderPath;
	private long pollingInterval;

	private FilenameFilter jsonFilter;
	private Gson blockFileParser;

	private File[] lastBlocks;

	private List<IBlockListener> blockListeners;

	private boolean stopFlag;

	public DemoBlockReceiver(String blockFolderPath, long pollingInterval) {
		// if the first parameter is null
		if (blockFolderPath == null) {
			throw new IllegalArgumentException("parameter \"blockFolderPath\" must not be null");
		}

		File folder = new File(blockFolderPath);

		// if the first parameter points to a *normal* file
		if (folder.exists() && folder.isFile()) {
			throw new IllegalArgumentException("parameter \"blockFolderPath\" must be the path of a folder");
		}

		// if the first parameter points to a forbidden folder
		if (folder.exists() && !folder.canRead()) {
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

		// if the second parameter is negative
		if (pollingInterval < 0) {
			throw new IllegalArgumentException("parameter \"pollingInverval\" must be positive");
		}

		// assign the first parameter to the member
		this.blockFolderPath = blockFolderPath;

		// assign the second parameter to the member
		this.pollingInterval = pollingInterval;

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

		this.blockFileParser = new GsonBuilder().create();

		// initialize the blocks, that the first poll does not report
		// new blocks
		this.lastBlocks = this.getBlocks();
		
		this.blockListeners = new ArrayList<>();

		this.stopFlag = false;
	}

	private File[] getBlocks() {
		File folder = new File(this.blockFolderPath);
		File[] blocks = folder.listFiles(jsonFilter);
		return blocks;
	}

	@Override
	public void addBlockListener(IBlockListener blockListener) {
		this.blockListeners.add(blockListener);
	}

	@Override
	public void removeBlockListener(IBlockListener blockListener) {
		this.blockListeners.remove(blockListeners);
	}

	public void notifyBlockReceived(Block block) {
		for (IBlockListener blockListener : this.blockListeners) {
			blockListener.onBlockReceived(block);
		}
	}

	@Override
	public void run() {
		System.out.println("started receiving blocks ...");
		while (!this.stopFlag) {
			try {
				File[] currentBlocks = this.getBlocks();

				if (currentBlocks.length != this.lastBlocks.length) {
					// TODO [joeren] was ist wenn mehrere Blöcke neu dazugekommen sind
					// dann müssen zwei Listen miteinander verglichen werden, sehr
					// hoher Aufwand!!!
					File newBlock = currentBlocks[currentBlocks.length - 1];
					FileReader fileReader = null;
					try {
						fileReader = new FileReader(newBlock);
						Block block = this.blockFileParser.fromJson(fileReader, Block.class);
						this.notifyBlockReceived(block);
					} catch (FileNotFoundException e) {
						System.err.println("error while receiving block");
					} finally {
						if (fileReader != null) {
							try {
								fileReader.close();
							} catch (IOException e) {
								System.err.println(e.getMessage());
							}
						}
					}
					
					this.lastBlocks = currentBlocks;
				}

				Thread.sleep(this.pollingInterval);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		System.out.println("stopped receiving blocks");
	}

	@Override
	public void stop() {
		if (!this.stopFlag) {
			this.stopFlag = true;
			System.out.println("initiated stop receiving blocks ...");
		}
	}

}
