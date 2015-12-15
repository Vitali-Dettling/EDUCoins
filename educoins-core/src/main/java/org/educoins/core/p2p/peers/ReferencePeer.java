package org.educoins.core.p2p.peers;

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
public class ReferencePeer extends Peer {

	//TODO only one public key will be used. Need to be improved in using multiple keys. 
	private static String publicKey;
	
	public ReferencePeer(BlockChain blockChain) {
		super(blockChain.getHttpProxyPeerGroup());
		Peer.blockChain = blockChain;
		publicKey = Peer.blockChain.getWallet().getPublicKey();
		Peer.client = new Client(Peer.blockChain);
	}

	@Override
	public void start() throws DiscoveryException {
		remoteProxies.discover();
		Block genesisBlock = new Block();
		//Kick starts the receiving of blocks. 
		remoteProxies.receiveBlocks(genesisBlock.hash());
		client();
	}

	private void client() {

		boolean running = true;
		while (running) {
			int own = 0;
			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action: (Amount: " + own + ")");
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
				own = getAmountInput();
				System.out.println("Send to address: " + ReferencePeer.publicKey);
				break;
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
				trans = Peer.client.sendApprovedTransaction(amount, owner, holder, lockingScript);
				System.out.println(System.currentTimeMillis() - time);
				if (trans != null)
					System.out.println(trans.hash());
				break;
			case "x":
				Sha256Hash hash = Sha256Hash
						.wrap(Peer.client.getHexInput(scanner, "Type in hash of transaction to revoke: "));
				trans = Peer.client.findTransaction(hash);
				Transaction revoke = Peer.client.sendRevokeTransaction(trans);
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
	
	// TODO Bad performance, each time the whole blockchain will be searched.
	//TODO does not work: how to find the own transactions (outputs).
	private int getAmountInput() {
		IBlockStore store = Peer.blockChain.getBlockStore();
		IBlockIterator iterator = store.iterator();
		int availableAmount = 0;
		try {
			while (iterator.hasNext()) {
				for (Transaction tx : iterator.next().getTransactions()) {
					for (Output outs : tx.getOutputs()) {
						if(outs.getLockingScript().contains(publicKey)){
							availableAmount += outs.getAmount();
						}
					}
				}
			}
		} catch (BlockNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return availableAmount;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
