package org.educoins.core;

public interface IBlockReceiver {

	/**
	 * Registers an {@link IBlockListener} which will be notified whenever a {@link Block} has been received.
	 *
	 * @param blockListener the BlockListener to register.
	 */
	void addBlockListener(IBlockListener blockListener);

	/**
	 * Detaches a specific {@link IBlockListener}. This {@link IBlockListener} will then not be notified anymore.
	 * @param blockListener the BlockListener to detach.
	 */
	void removeBlockListener(IBlockListener blockListener);

	/**
	 * Fetches Blocks.
	 */
	void receiveBlocks();
	
}
