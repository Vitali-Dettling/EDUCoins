package org.educoins.core.p2p.peers;

import java.util.Scanner;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.Miner;
import org.educoins.core.Transaction;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.utils.Sha256Hash;

/**
 * The {@link Peer}-Type having only reading-capabilities. Created by typus on
 * 11/3/15.
 */
public class SoloMinerPeer extends Peer {

	private Miner miner;

	public SoloMinerPeer(BlockChain blockChain, Miner miner) {
		Peer.blockChain = blockChain;
		this.miner = miner;

		IProxyPeerGroup peerGroup = new HttpProxyPeerGroup();
		this.miner.setBlockChain(blockChain);
		miner.addPoWListener(peerGroup);
	}

	@Override
	public void start() throws DiscoveryException {

		// Kick off Miner.
		blockChain.foundPoW(new Block());
		client();
	}

	private void client() {

		Client client = new Client(this.blockChain);

		boolean running = true;
		while (running) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action:");
			System.out.println("\t - (R)egular transaction");
			System.out.println("\t - (E)Exit");
			String action = scanner.nextLine();
			int amount = -1;
			Transaction trans = null;
			switch (action.toLowerCase()) {
			case "r":
				amount = client.getIntInput(scanner, "Type in amount: ");
				if (amount == -1)
					continue;
				String dstPublicKey = client.getHexInput(scanner, "Type in dstPublicKey: ");
				if (dstPublicKey == null)
					continue;
				trans = client.sendRegularTransaction(amount, dstPublicKey, dstPublicKey);
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
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
