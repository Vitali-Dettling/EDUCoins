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
	private static int amount = 0;

	public SoloMinerPeer(BlockChain blockChain) {
		Peer.blockChain = blockChain;	
	}

	@Override
	public void start() throws DiscoveryException {
		this.miner = Peer.blockChain.getMiner();
		Peer.client = new Client(Peer.blockChain);

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
			int amount = -1;
			Transaction trans = null;
			int own = getAmount();
			switch (action.toLowerCase()) {
			case "g":
				System.out.println("Owen EDUCoins " + own);
				break;
			case "r":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				if (amount == -1)
					continue;
				String dstPublicKey = Peer.client.getHexInput(scanner, "Type in dstPublicKey: ");
				if (dstPublicKey == null)
					continue;
				trans = Peer.client.sendRegularTransaction(amount, dstPublicKey, dstPublicKey, own);
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
	public int getAmount(){
//		Wallet wallet = Peer.blockChain.getWallet();
//		List<String> publickeys = wallet.getPublicKeys();
//		
//		for(String key : publickeys){
//			SoloMinerPeer.amount = Peer.client.getAmount(key);
//		}
		return -1;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
	
}
