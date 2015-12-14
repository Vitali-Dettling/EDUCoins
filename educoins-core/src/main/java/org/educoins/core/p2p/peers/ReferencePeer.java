package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.utils.Sha256Hash;

import java.util.Scanner;

/**
 * The Reference Client consisting of a Miner, a {@link BlockChain} and a
 * {@link Wallet}. Created by typus on 11/23/15.
 */
public class ReferencePeer extends Peer {

	public ReferencePeer(BlockChain blockChain) {
		super(blockChain.getHttpProxyPeerGroup());
		this.blockChain = blockChain;

	}

	@Override
	public void start() throws DiscoveryException {
		remoteProxies.discover();
		remoteProxies.receiveBlocks(new Block().hash());
		client();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	private void client() {

		Client client = new Client(this.blockChain);
		Wallet wallet = this.blockChain.getWallet();

		boolean running = true;
		while (running) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action:");
			System.out.println("\t - (P)Get Public Key");
			System.out.println("\t - (G)Received EDUCoins");
			System.out.println("\t --- Transactions types ---");
			System.out.println("\t - (R)egular transaction");
			System.out.println("\t - (A)pproved transaction");
			System.out.println("\t - (X)Revoke transaction");
			System.out.println("\t - (B)reak client");
			String action = scanner.nextLine();
			int amount = -1;
			Transaction trans = null;
			switch (action.toLowerCase()) {
			case "p":
				String publicKey = wallet.getPublicKey();
				System.out.println("Send to address: " + publicKey);
				break;
			case "g":
				System.out.println("Received EDUCoins: " + client.getAmountInput());
				break;
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
			case "a":
				amount = client.getIntInput(scanner, "Type in amount: ");
				if (amount == -1)
					continue;
				System.out.print("Type in owner: ");
				String owner = scanner.nextLine();
				System.out.print("Type in holder: ");
				String holder = scanner.nextLine();
				System.out.print("Type in LockingScript: ");
				String lockingScript = scanner.nextLine();
				long time = System.currentTimeMillis();
				trans = client.sendApprovedTransaction(amount, owner, holder, lockingScript);
				System.out.println(System.currentTimeMillis() - time);
				if (trans != null)
					System.out.println(trans.hash());
				break;
			case "x":
				Sha256Hash hash = Sha256Hash
						.wrap(client.getHexInput(scanner, "Type in hash of transaction to revoke: "));
				trans = client.findTransaction(hash);
				Transaction revoke = client.sendRevokeTransaction(trans);
				if (revoke != null) {
					System.out.println("Revoked transaction: " + trans.hash());
					System.out.println("With Revoke: " + revoke.hash());
				}
				break;
			case "e":
				running = false;
				break;
			default:
			}
		}
	}
}
