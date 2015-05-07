package org.educoins.core;

import org.educoins.core.miner.Block;

public interface IBlockTransmitter {

	void sendBlock(Block block);
	
}
