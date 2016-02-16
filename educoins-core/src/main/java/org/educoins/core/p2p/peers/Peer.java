package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

	public Peer(BlockChain blockChain) {
		Peer.blockChain = blockChain;
		Peer.remoteProxies = new HttpProxyPeerGroup();
		Peer.client = new Client();
		Peer.blockChain.addBlockListener(this);
		Peer.remoteProxies.addBlockListener(blockChain);
	}

	public abstract void start() throws DiscoveryException;

	public abstract void stop();

	// region listeners

	@Override
	public void blockListener(Block receivedBlock) {
		logger.info("Received block. hash: {}", receivedBlock.hash());
		if (blockChain.contains(receivedBlock)) return;

		boolean stored = blockChain.storeBlock(receivedBlock);
		if (!stored) {
			// The received Block might be younger than the one we have stored,
			// so we try to get up to date.
			remoteProxies.receiveBlocks(blockChain.getLatestBlock().hash());
		} else {
			/* TODO: A new node might overwrite the Blockchain with its own Blocks.
			 The length (=combined difficulties) have to be taken into account
			 so that only the longest chain will survive.

			 Beware: When receiving a new, longer blockchain, the Blocks will be
			 transferred step by step, so unless the transfer is finished it is
			 actually shorter but may not be deleted.
			*/
			handleNewValidBlock(receivedBlock);
			Peer.client.ownTransactions(receivedBlock);

			List<Transaction> transactions = receivedBlock.getTransactions();
			if (transactions != null) {
				logger.info("Found {} transactions", transactions.size());
				for (Transaction transaction : transactions) {
					blockChain.notifyTransactionReceived(transaction);
				}
			}

		}
		logger.info("Block processed.");
	}

	/**
	 * Specific implementation for handling a new valid Block.
	 * This template method is called by the {@link Peer#blockListener(Block)}
     */
	protected abstract void handleNewValidBlock(Block block);

	@Override
	public void addBlockListener(IBlockListener blockListener) {
		Peer.blockChain.addBlockListener(blockListener);
		Peer.remoteProxies.addBlockListener(blockListener);
	}

	@Override
	public void receiveBlocks(Sha256Hash from) {
		Peer.remoteProxies.receiveBlocks(from);
	}

	
	@Override
	public void removeBlockListener(IBlockListener blockListener) {
		Peer.blockChain.removeBlockListener(blockListener);
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
