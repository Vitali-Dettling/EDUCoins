package org.educoins.core.p2p.peers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.educoins.core.*;
import org.educoins.core.config.AppConfig;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.utils.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PeerNode representation. Necessary for P2P Networking. The concrete
 * implementations are the following: Reference Client->miner,blockchain,wallet
 * Full BlockChain->blockchain Solo Miner->miner,blockchain Created by typus on
 * 10/27/15.
 */
public abstract class Peer implements IBlockReceiver, IBlockListener {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected static IProxyPeerGroup remoteProxies;
	protected static BlockChain blockChain;
	protected static Client client;
	protected static Wallet wallet;
	private static Sha256Hash proxyPublicKey;

	protected static final Set<IBlockListener> blockListeners = new HashSet<>();

	public Peer(IProxyPeerGroup remoteProxies) {
		Peer.remoteProxies = remoteProxies;		
		Peer.proxyPublicKey = AppConfig.getOwnPublicKey();
	}

	public abstract void start() throws DiscoveryException;

	public abstract void stop();

	// region listeners

	@Override
	public void blockListener(Block receivedBlock) {
		Peer.client.distructOwnOutputs(receivedBlock);
		boolean result = Peer.blockChain.verifyReceivedBlock(receivedBlock);

		if (result) {
			// Tries as long as the blockchain is up to date.
			Block latestBlock = blockChain.getLatestBlock();
			Peer.remoteProxies.receiveBlocks(latestBlock.hash());
		}
	}

	@Override
	public void receiveBlocks(Sha256Hash from) {
		Peer.remoteProxies.receiveBlocks(from);
	}

	public void addBlockListener(IBlockListener blockListener) {
		Peer.blockListeners.add(blockListener);
		Peer.remoteProxies.addBlockListener(blockListener);

	}

	@Override
	public void removeBlockListener(IBlockListener blockListener) {
		Peer.blockListeners.remove(blockListener);
		Peer.remoteProxies.removeBlockListener(blockListener);
	}
	// endregion

	@Override
	public int hashCode() {
		int result = logger != null ? logger.hashCode() : 0;
		result = 31 * result + (remoteProxies != null ? remoteProxies.hashCode() : 0);
		return result;
	}

	// region equality
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Peer peer = (Peer) o;

		return !(logger != null ? !logger.equals(peer.logger) : peer.logger != null)
				&& !(remoteProxies != null ? !remoteProxies.equals(Peer.remoteProxies) : Peer.remoteProxies != null);

	}
	// endregion

}
