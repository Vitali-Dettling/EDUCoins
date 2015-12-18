package org.educoins.core.p2p.peers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.Sha256Hash;

/**
 * The Reference Client consisting of a Miner, a {@link BlockChain} and a
 * {@link Wallet}. Created by typus on 11/23/15.
 */
public class ReferencePeer extends Peer implements ITransactionTransmitter {

	// TODO only one public key will be used. Need to be improved in using
	// multiple keys.
	private static String singlePublicKey;

	public ReferencePeer(BlockChain blockChain) {
		super(new HttpProxyPeerGroup());
		Peer.wallet = new Wallet();
		Peer.client = new Client(wallet);
		Peer.blockChain = new BlockChain(wallet);
		singlePublicKey = Peer.wallet.getPublicKey();
	}

	public int getAmount() {
		return ReferencePeer.client.getAmount();
	}

	public void setPubKey(String publicKey) {
		ReferencePeer.singlePublicKey = publicKey;
	}

	public String getPubKey() {
		return ReferencePeer.singlePublicKey;
	}

	@Override
	public void start() throws DiscoveryException {
		//Peer.remoteProxies.discover();
		Block genesisBlock = new Block();
		// Kick starts of receiving of blocks.
		remoteProxies.receiveBlocks(genesisBlock.hash());
		client();
	}

	private void client() {

		boolean running = true;
		while (running) {

			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action: ");
			System.out.println("\t - (P)Get Public Key");
			System.out.println("\t - (G)Get Own EDUCoins");
			System.out.println("\t --- Transactions types ---");
			System.out.println("\t - (R)egular transaction");
			System.out.println("\t - (A)pproved transaction");
			System.out.println("\t - (X)Revoke transaction");
			System.out.println("\t - (E)xit");
			String action = scanner.nextLine();
			int amount = -1;
			Transaction trans = null;
			switch (action.toLowerCase()) {
			case "p":
				System.out.println("Send to address: " + ReferencePeer.singlePublicKey);
				break;
			case "g":
				System.out.println("Owen EDUCoins " + Peer.client.getAmount());
				break;
			case "r":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				int availableAmount = Peer.client.getAmount();
				if (amount > availableAmount) {
					System.err.println("Not enough available amount (max. " + availableAmount + ")");
					break;
				}
				String dstPublicKey = Peer.client.getHexInput(scanner, "Type in dstPublicKey: ");
				if (dstPublicKey == null)
					continue;
				trans = Peer.client.sendRegularTransaction(amount, dstPublicKey);
				ReferencePeer.blockChain.sendTransaction(trans);
				if (trans != null)
					System.out.println(trans.hash());
				break;
			case "a":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				if (amount == -1)
					continue;
				System.out.print("Type in owner: ");
				String owner = scanner.nextLine();
				System.out.print("Type in holder: ");
				String holder = scanner.nextLine();
				System.out.print("Type in LockingScript: ");
				String lockingScript = scanner.nextLine();
				long time = System.currentTimeMillis();
				// TODO
				// trans = Peer.client.sendApprovedTransaction(amount, owner,
				// holder, lockingScript);
				System.out.println(System.currentTimeMillis() - time);
				if (trans != null)
					System.out.println(trans.hash());
				break;
			case "x":
				// TODO
//				 Sha256Hash hash = Sha256Hash.wrap(trans.hash().toString());
//				 Peer.client.getHexInput(scanner, "Type in hash of transaction to revoke: ");
//				 trans = Peer.client.findTransaction(hash);
//				 Transaction revoke = Peer.client.sendRevokeTransaction(trans);
//				 if (revoke != null) {
//				 System.out.println("Revoked transaction: " + trans.hash());
//				 System.out.println("With Revoke: " + revoke.hash());
//				 }
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

	// region listeners
	
	@Override
	public void transmitTransaction(Transaction transaction) {
		remoteProxies.transmitTransaction(transaction);
	}

	// endregion
}
