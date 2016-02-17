package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.transaction.Revoke;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.Transaction.ETransaction;
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

	@Override
	protected void handleNewValidBlock(Block block) {
		// No special behavior for reference peer so far
	}

	private void client() {

		boolean running = true;
		while (running) {

			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action: ");
			System.out.println("\t - (P)ublic Key");
			System.out.println("\t - (S)ignature");
			System.out.println("\t - (G)et Own EDUCoins");
			System.out.println("\t - (L)ist of Transactions");
			System.out.println("\t - (C)heck approved EDUCoins");
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
				String randomSignature = Wallet.getSecureRandomString256HEX();
				String signature = Wallet.getSignature(ReferencePeer.singlePublicKey, randomSignature);
				System.out.println("Created Signature: " + signature);
			case "g":
				System.out.println("Regular EDUCoins " + Peer.client.getEDUCoinsAmount());
				System.out.println("Approved EDUCoins " + Peer.client.getApprovedCoins());
				break;
			case "r":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				int availableAmount = Peer.client.getEDUCoinsAmount();
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
				System.out.print("Type in public key: ");
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
				trans = client.generateRevokeTransaction(transHash);
			
				if (trans != null) {
					ReferencePeer.blockChain.sendTransaction(trans);
					System.out.println("With Revoke: " + trans.hash());
				}
				break;
			case "l":
				List<TransactionVM> vm = client.getListOfTransactions(Peer.blockChain);
				for (TransactionVM t : vm) {
					System.out.print("-> Transaction:\t Type: " + t.getTransactionType().toString() + "\t| Hash: " + t.getHash() + "\t| ");
					if(t.getTransactionType() == ETransaction.REVOKE){
						Transaction tx = Peer.blockChain.getTransaction(t.getHash());
						System.out.print("Revoked: " + tx.getRevokes().get(0).getHashPrevApproval() + "\t|");
					}
					System.out.println();
				}
				break;
			case "c":
				System.out.println("Please, enter the approved hash: ");
				String stillApproved = scanner.nextLine();
				
				ETransaction result = Peer.blockChain.approvalValide(stillApproved);
				
				if(ETransaction.APPROVED == result){
					System.out.println("The approved educoins are still valide.");
				}
				else if(ETransaction.REVOKE == result){
					System.out.println("The educoins had been revoked.");
				}else{
					System.out.println("It is not a approved transaction hash.");
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
