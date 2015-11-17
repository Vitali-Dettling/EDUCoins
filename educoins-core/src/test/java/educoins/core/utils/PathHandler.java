package educoins.core.utils;

import java.io.File;

public class PathHandler {
	
	public static File DIRECTORY_DB = new File(System.getProperty("user.home") + File.separator + "documents" + File.separator
            + "educoins" + File.separator + "test" + File.separator + "BlockChain" + File.separator + "blockstore") ;
	
	public static String DIRECTORY_WALLET = System.getProperty("user.home") + File.separator + "documents" + File.separator
            + "educoins" + File.separator + "test" + File.separator + "BlockChain" + File.separator + "blockstore" ;
	
}
