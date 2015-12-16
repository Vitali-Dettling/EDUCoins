package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;

import java.util.List;
import java.util.Scanner;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.Miner;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;

/**
 * The {@link Peer}-Type having only reading-capabilities. Created by typus on
 * 11/3/15.
 */
public class SoloMinerPeer extends Peer {

	private Miner miner;
	private static Wallet wallet;
	private static String reversPublicKey;

	public SoloMinerPeer(BlockChain blockChain) {
		Peer.blockChain = blockChain;
		SoloMinerPeer.wallet = Peer.blockChain.getWallet();
		reversPublicKey = SoloMinerPeer.wallet.getPublicKey();
		this.miner = Peer.blockChain.getMiner();
		Peer.client = new Client(Peer.blockChain);
	}

	@Override
	public void start() throws DiscoveryException {
		IProxyPeerGroup peerGroup = Peer.blockChain.getHttpProxyPeerGroup();
		this.miner.setBlockChain(blockChain);
		miner.addPoWListener(peerGroup);

		// Kick off Miner.
		Block block = new Block();
		blockChain.foundPoW(block);
		client();
	}

	private void client() {

		boolean running = true;
		while (running) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action:");
			System.out.println("\t - (G)Get Own EDUCoins");
			System.out.println("\t - (R)egular transaction");
			System.out.println("\t - (E)xit");
			String action = scanner.nextLine();
			Transaction trans = null;
			int amount = -1;
			switch (action.toLowerCase()) {
			case "g":
				System.out.println("Owen EDUCoins " + getAmount());
				break;
			case "r":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				if (amount == -1)
					continue;
				String lockingScript = Peer.client.getHexInput(scanner, "Type in dstPublicKey: ");
				if (lockingScript == null)
					continue;
				trans = Peer.client.sendRegularTransaction(amount, SoloMinerPeer.reversPublicKey, lockingScript);
				if (trans != null)
					System.out.println(trans.hash());
				break;
			case "e":
				running = false;
				break;
			default:
			}
		}
	}

	@Override
	public int getAmount() {

		List<String> publickeys = wallet.getPublicKeys();
		return Peer.client.getAmount(publickeys);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	public String getPubKey() {
		return SoloMinerPeer.wallet.getPublicKey();
	}

}
