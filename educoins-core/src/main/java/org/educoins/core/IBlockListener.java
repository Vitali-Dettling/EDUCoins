package org.educoins.core;

public interface IBlockListener {

	/**
	 * Called whenever a new {@link Block} was detected.
	 *
	 * @param block: The newly detected block.
	 */
	void blockReceived(Block block);
	
}
