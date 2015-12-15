package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.discovery.DiscoveryException;

/**
 * The {@link Peer}-type representing a Peer with full capabilities.
 * Created by typus on 11/3/15.
 */
public class FullBlockChainPeer extends Peer {
    public FullBlockChainPeer(BlockChain blockChain) {
        this.blockChain = blockChain;
    }
    
	@Override
    public void start() throws DiscoveryException {
		//TODO Does notthing right now.
//      remoteProxies.discover();
//      blockChain.foundPoW(new Block());
  }

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAmount() {
		return 0;
		// TODO Auto-generated method stub
		
	}
}
