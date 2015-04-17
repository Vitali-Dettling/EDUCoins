package org.educoins.core.miner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class BlockChain {

	private Block block;
	private static int blockCount = 0;
	private File directory;
	private PrintWriter newBlock;

	public BlockChain(Block block) {

		this.block = block;
		this.directory = new File("C://Users//admin//Documents//BlockChain");

		blockContent();
	}

	private void blockContent() {

		if (!directory.isDirectory()) {
			this.directory.mkdirs();
		}

		try {

			this.newBlock = new PrintWriter(this.directory.getPath()
					+ "//Block_" + this.blockCount++, "UTF-8");

			this.newBlock.println(Integer.toString(this.block.getVersion()));
			this.newBlock.println(this.block.getHashedPrevBlock());
			this.newBlock.println(this.block.getHashedMerkleRoot());
			this.newBlock.println(Integer.toString(this.block.getTimeStamp()));
			this.newBlock.println(Integer.toString(this.block.getDifficulty()));
			this.newBlock.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			System.err.println("ERROR: Class BlockChain");
		}

	}

}
