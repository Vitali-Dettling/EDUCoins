package org.educoins.core.miner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class BlockChain {

	private File blockChainFile;

	public BlockChain() throws IOException {
		blockChainFile = new File("blockchain.txt");
		if (!blockChainFile.exists()) {
			try {
				blockChainFile.createNewFile();
			} catch (IOException e) {
				System.err.printf("Error while creating block chain file: "
						+ "%s\r\n", e.toString());
				throw e;
			}
		}
	}

	public void addBlock(Block block) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileOutputStream(blockChainFile, true));
			writer.println(block);
		} catch (IOException e) {
			System.err.printf("Error while writing to block chain file: "
					+ "%s\r\n", e.toString());
		} finally {
			writer.close();
		}
	}

}
