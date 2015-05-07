package org.educoins.core;

public interface IBlockReceiver extends Runnable {
	
	void addBlockListener(IBlockListener listener);
	
	void removeBlockListener(IBlockListener listener);
	
	void stop();
	
}
