package org.educoins.core.p2p.peers;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Miner;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.store.BlockNotFoundException;

/**
 * The {@link Peer}-Type having only reading-capabilities. Created by typus on
 * 11/3/15.
 */
public class SoloMinerPeer extends Peer {

	private Miner miner;

	public SoloMinerPeer(BlockChain blockChain, Miner miner) {
		Peer.blockChain = blockChain;
		this.miner = miner;
		Peer.type = PeerType.MINER;

		IProxyPeerGroup peerGroup = new HttpProxyPeerGroup();
		this.miner.setBlockChain(blockChain);
		miner.addPoWListener(peerGroup);
	}

	@Override
	public void start() throws DiscoveryException {

		// Kick off Miner.
		blockChain.foundPoW(new Block());

	}
}
