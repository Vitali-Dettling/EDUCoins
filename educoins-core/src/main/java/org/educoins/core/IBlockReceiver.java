package org.educoins.core;

import org.educoins.core.utils.Sha256Hash;

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
	 * Fetches Blocks which are younger then the specified hash.
	 * @param from the hash to get the older ones from.
	 */
	void receiveBlocks(Sha256Hash from);
	
}
