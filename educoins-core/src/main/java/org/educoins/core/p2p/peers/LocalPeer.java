package org.educoins.core.p2p.peers;

import org.educoins.core.p2p.discovery.DiscoveryException;

/**
 * The Peer used for Testing.
 * Created by typus on 11/5/15.
 */
public class LocalPeer extends Peer {

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
