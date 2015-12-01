package org.educoins.core.utils;

import java.io.File;
import java.io.IOException;

public class MockedIO {
	
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	
    public static File getDefaultBlockStoreFile() {
        return new File(TMP_DIR + FILE_SEPERATOR + "EDUCoins" + FILE_SEPERATOR + "BlockStore");
    }

    public static boolean deleteDefaultBlockStoreFile() {
        try {
            IO.deleteDirectory(TMP_DIR + FILE_SEPERATOR + "EDUCoins" + FILE_SEPERATOR + "BlockStore");
        } catch (IOException e) {
        	System.err.println("Cannot be deleted: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    public static File getDefaultWalletStore() {
        return new File(TMP_DIR + FILE_SEPERATOR + "EDUCoins" + FILE_SEPERATOR + "Wallet");
    }

    public static boolean deleteDefaultWalletStore() {
        try {
            IO.deleteDirectory(TMP_DIR + FILE_SEPERATOR +  "EDUCoins" + FILE_SEPERATOR + "Wallet");
        } catch (IOException e) {
        	System.err.println("Cannot be deleted: " + e.getMessage());
            return false;
        }
        return true;
    }

}
