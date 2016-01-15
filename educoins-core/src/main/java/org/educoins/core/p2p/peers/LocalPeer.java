package org.educoins.core.p2p.peers;

import org.educoins.core.BlockChain;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.utils.Sha256Hash;

/**
 * The Peer used for Testing.
 * Created by typus on 11/5/15.
 */
public class LocalPeer extends Peer {

    protected LocalPeer(BlockChain blockChain, IProxyPeerGroup proxyPeerGroup, Sha256Hash publicKey) {
        super(blockChain, proxyPeerGroup, publicKey);
    }

    @Override
    public void start() throws DiscoveryException {
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
