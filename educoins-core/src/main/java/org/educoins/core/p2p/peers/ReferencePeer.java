package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.Sha256Hash;

import java.util.List;
import java.util.Scanner;

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

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

	private void client() {

		boolean running = true;
		while (running) {

			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action: ");
			System.out.println("\t - (P)Get Public Key");
			System.out.println("\t - (S)Create Signature");
			System.out.println("\t - (G)Get Own EDUCoins");
			System.out.println("\t - (L)ist of all Transactions");
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
			case "s":
				String hashTx = "123456789ABCDEF";
				String signature = Wallet.getSignature(ReferencePeer.singlePublicKey, hashTx);
				System.out.println("Created Signature: " + signature);
			case "g":
				System.out.println("Regular EDUCoins " + Peer.client.getEDICoinsAmount());
				System.out.println("Approved EDUCoins " + Peer.client.getApprovedCoins());
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
				if (trans != null){
					ReferencePeer.blockChain.sendTransaction(trans);
					System.out.println("Hash of created transaction: " + trans.hash());
				}
				break;
			case "a":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				if (amount == -1)
					continue;
				System.out.print("Owner address is: " + ReferencePeer.singlePublicKey + "\n");
				String owner = ReferencePeer.singlePublicKey;
				System.out.print("Type in LockingScript: ");
				String lockingScript = scanner.nextLine();
				System.out.print("Holder signature is for: ");
				String holderSignature = scanner.nextLine();

				trans = Peer.client.generateApprovedTransaction(amount, owner, holderSignature, lockingScript);
				if (trans != null){
					ReferencePeer.blockChain.sendTransaction(trans);
					System.out.println(trans.hash());
				}
				break;
			case "x":
				String transHash = client.getHexInput(scanner, "Type in hash of transaction to revoke: ");
				Sha256Hash hash = Sha256Hash.wrap(transHash);
				//TODO lockingScript needs to be implemented
//				System.out.print("Type in LockingScript: ");
//				String lockingScript = scanner.nextLine();
				Transaction transToRevoke = blockChain.getTransaction(hash);
				if (transToRevoke == null) {
					System.out.println("Could not find transaction.");
				}
				else {
					trans = client.generateRevokeTransaction(hash, "");
					if (trans != null) {
						ReferencePeer.blockChain.sendTransaction(trans);
						System.out.println("Revoked transaction: " + transToRevoke.hash());
						System.out.println("With Revoke: " + trans.hash());
					}
				}
				break;
			case "l":
				List<TransactionVM> vm = client.getListOfTransactions(Peer.blockChain);
				for (TransactionVM t : vm) {
					System.out.print("-> Transaction:\t Type: " + t.getTransactionType().toString() + "\t| Hash: " + t.getHash() + "\t| ");
					System.out.print("PubKey: " + t.getReceiver() + "\t|");
					System.out.print("Amount: " + t.getAmount() + "\n");
				}
				break;
			case "e":
				running = false;
				break;
			default:
			}
		}
	}

	// region listeners

	@Override
	public void transmitTransaction(Transaction transaction) {
		Peer.remoteProxies.transmitTransaction(transaction);
	}

	// endregion
}
