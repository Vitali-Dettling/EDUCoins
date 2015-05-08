package org.educoins.core.miner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public final class BlockChain {

	private static int blockCount = 0;
	private static File directory;
	private static PrintWriter newBlock;


	public static void setAddress(String blockChainDirectory) {
		directory = new File(blockChainDirectory);
		
	}
	
	public static void newBlock(Block block) {

		if (!directory.isDirectory()) {
			directory.mkdirs();
		}
		
		//Löschen nachdem das Program fertig ist, weil er den BlockChain ordner nicht löschen soll, sondern immer erweitern...
		if(blockCount == 0){
			for(String s: directory.list()){
			    File currentFile = new File(directory.getPath(),s);
			    currentFile.delete();
			}
		}//!!! Bis hier Löschen !!!
		
		try {
			newBlock = new PrintWriter(directory.getPath()
					+ "//Block_" + blockCount++, "UTF-8");

			newBlock.println(block);
			newBlock.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			System.err.println("ERROR: Class BlockChain");
		}

	}





}
