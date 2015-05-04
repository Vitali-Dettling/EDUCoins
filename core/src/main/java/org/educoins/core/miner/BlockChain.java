package org.educoins.core.miner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class BlockChain {

	private static int blockCount = 0;
	private File directory;
	private PrintWriter newBlock;

	public BlockChain() {

		this.directory = new File("./../../../BlockChain");
	}

	public void newBlock(Block block) {

		//Löschen nachdem das Program fertig ist, weil er den BlockChain ordner nicht löschen soll, sondern immer erweitern...
		if(this.blockCount == 0){
			for(String s: this.directory.list()){
			    File currentFile = new File(this.directory.getPath(),s);
			    currentFile.delete();
			}
		}//!!! Bis hier Löschen !!!
		
		if (!directory.isDirectory()) {
			this.directory.mkdirs();
		}

		try {
			this.newBlock = new PrintWriter(this.directory.getPath()
					+ "//Block_" + this.blockCount++, "UTF-8");

			this.newBlock.println(block);
			this.newBlock.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			System.err.println("ERROR: Class BlockChain");
		}

	}

}
