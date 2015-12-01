package org.educoins.core.utils;

import org.educoins.core.Block;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;

public class MockedStore {

	private static IBlockStore store;

	static{
		try {
			store = new LevelDbBlockStore(MockedIO.getDefaultBlockStoreFile());
		} catch (BlockStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static IBlockStore getStore() {
		return store;
	}
	
	public static void put(Block block){
		store.put(block);
	}
	
	public static Block get(byte[] hash){
		return store.get(hash);
	}
	
	public static Block getLatest(){
		return store.getLatest();
	}
	
	public static void delete(){
		try {
			store.destroy();
		} catch (BlockStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
