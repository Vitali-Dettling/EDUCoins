package org.educoins.core;

public interface IBlockReceiver {

	void addBlockListener(IBlockListener blockListener);
	
	void removeBlockListener(IBlockListener blockListener);
	
	void receiveBlocks();
	
}
