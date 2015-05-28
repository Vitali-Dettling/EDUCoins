package org.educoins.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Deserializer;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public abstract class Verifier {
	
	private static final int TRUE = 0;
	private static final String GENIUSES_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";

	public static boolean verifyBlock(Block testblock){

		//0. If geniuses block return true, because there no other block before.
		if(testblock.getHashPrevBlock().equals(GENIUSES_BLOCK)){
			return true;
		}
		
		//1. Find the previous block.
		Block lastBlock = getPreviousBlock(testblock);

		//2. Does the previous block exist?
		if(lastBlock == null){
			return false;
		}
		
		//3. Are the hashes equal of the current block and the previous one?
		byte[] testBlockHash = testblock.hash();
		byte[] lastBlockHash = lastBlock.getHashPrevBlock().getBytes();
		if(ByteArray.compare(testBlockHash, lastBlockHash) == TRUE){
			return false;
		}
		
		//TODO[Vitali] Überlegen ob weitere Test von nöten wären???
		
		return true;
	
	}
	
	
	
	public boolean verifyTransaction(ATransaction transaction){
		
		
		//TODO[Vitali] Implement verification for transactions, which will be needed for the miner.
		
		
		
		
		return true;
		
	}

	
	private static Block getPreviousBlock(Block testblock){
		try {
		
		String lastBlockName = testblock.getHashPrevBlock();
		
		//TODO[Vitali] Der remoteStorage String ist nur für den Prototypen, sollte geändert werden sobal eine levelDB eingeführt wird!!!
		String remoteStoragePath = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";
		
			return Deserializer.deserialize(remoteStoragePath, lastBlockName);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			System.out.println("ERROR: Class Verifier: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	

	

}
