package org.educoins.core;

import org.educoins.core.miner.Block;

public interface IBlockListener {

	void onBlockReceived(Block block);
	
}
