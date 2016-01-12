package org.educoins.core.p2p.peers;

import java.util.Scanner;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.ITransactionTransmitter;
import org.educoins.core.Wallet;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.transaction.Transaction;

/**
 * The Reference Client consisting of a Miner, a {@link BlockChain} and a
 * {@link Wallet}. Created by typus on 11/23/15.
 */
public class ReferencePeer extends Peer implements ITransactionTransmitter {

	// TODO only one public key will be used. Need to be improved in using
	// multiple keys.
	private static String singlePublicKey;

	public ReferencePeer(BlockChain blockChain) {
		super(blockChain);
		singlePublicKey = Wallet.getPublicKey();
		Peer.remoteProxies.addBlockListener(this);
	}

	public String getPubKey() {
		return ReferencePeer.singlePublicKey;
	}

	@Override
	public void start() throws DiscoveryException {
		Peer.remoteProxies.discover();
		// Kick starts of receiving of blocks.
		Block genesisBlock = new Block();
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
				System.out.println("Regular EDUCoins " + Peer.client.getEDICoinsAmount());
				System.out.println("Approved EDUCoins " + Peer.client.getApproveCoins());
				break;
			case "r":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				int availableAmount = Peer.client.getEDICoinsAmount();
				if (amount > availableAmount) {
					System.err.println("Not enough available amount (max. " + availableAmount + ")");
					break;
				}
				String dstPublicKey = Peer.client.getHexInput(scanner, "Type in dstPublicKey: ");
				if (dstPublicKey == null)
					continue;
				trans = Peer.client.generateRegularTransaction(amount, dstPublicKey);
				if (trans != null)
					ReferencePeer.blockChain.sendTransaction(trans);
					System.out.println(trans.hash());
				break;
			case "a":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				if (amount == -1)
					continue;
				System.out.print("Owner address is: " + ReferencePeer.singlePublicKey + "\n");
				String owner = ReferencePeer.singlePublicKey;
				System.out.print("Type in LockingScript: ");
				String lockingScript = scanner.nextLine();
				
				trans = Peer.client.generateApprovedTransaction(amount, owner, lockingScript);
				if(!trans.getApprovals().isEmpty()){
					String holderSignature = trans.getApprovals().get(0).getHolderSignature();
					System.out.print("Holder signature is: " + holderSignature);
				}
				long time = System.currentTimeMillis();
				System.out.println(System.currentTimeMillis() - time);
				if (trans != null){
					ReferencePeer.blockChain.sendTransaction(trans);
					System.out.println(trans.hash());
				}
				break;
			case "x":
				// TODO
				// Sha256Hash hash = Sha256Hash.wrap(trans.hash().toString());
				// Peer.client.getHexInput(scanner, "Type in hash of transaction
				// to revoke: ");
				// trans = Peer.client.findTransaction(hash);
				// Transaction revoke =
				// Peer.client.sendRevokeTransaction(trans);
				// if (revoke != null) {
				// System.out.println("Revoked transaction: " + trans.hash());
				// System.out.println("With Revoke: " + revoke.hash());
				// }
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
		Peer.remoteProxies.transmitTransaction(transaction);
	}

	// endregion
}
