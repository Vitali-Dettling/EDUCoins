package org.educoins.core.p2p.peers;

import java.util.HashMap;
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
public class ReferencePeer extends Peer {

	//TODO only one public key will be used. Need to be improved in using multiple keys. 
	private static String publicKey;
	private static int availableAmount = 0;
	private static Map<String, String> lastBlockMap;
	
	public ReferencePeer(BlockChain blockChain) {
		super(blockChain.getHttpProxyPeerGroup());
		Peer.blockChain = blockChain;
		publicKey = Peer.blockChain.getWallet().getPublicKey();
		Peer.client = new Client(Peer.blockChain);
		
		// Genesis block.
		ReferencePeer.lastBlockMap = new HashMap<String, String>();
		Block genesis = new Block();
		ReferencePeer.lastBlockMap.put(genesis.getHashPrevBlock().toString(), genesis.getHashMerkleRoot().toString());
	}
	
	public void setPubKey(String publicKey){
		ReferencePeer.publicKey = publicKey;
	}
	
	public String getPubKey(){
		return ReferencePeer.publicKey;
	}

	@Override
	public void start() throws DiscoveryException {
		remoteProxies.discover();
		Block genesisBlock = new Block();
		//Kick starts of receiving of blocks. 
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
			getAmount();
			switch (action.toLowerCase()) {
			case "p":
				System.out.println("Send to address: " + ReferencePeer.publicKey);
				break;
			case "g":
				System.out.println("Owen EDUCoins " + ReferencePeer.availableAmount);
				break;
			case "r":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				
				if (amount > ReferencePeer.availableAmount) {
					System.err.println("Not enough available amount (max. " + ReferencePeer.availableAmount + ")");
					break;
				}
				String dstPublicKey = Peer.client.getHexInput(scanner, "Type in dstPublicKey: ");
				if (dstPublicKey == null)
					continue;
				trans = Peer.client.sendRegularTransaction(amount, dstPublicKey, dstPublicKey, ReferencePeer.availableAmount);
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
	
	@Override
	public int getAmount() {

		IBlockIterator iterator = Peer.blockChain.getBlockStore().iterator();

		try {
			while (iterator.hasNext()) {
				Block block = iterator.next();
				// Break up as soon as the last searched block was found.
				if (ReferencePeer.lastBlockMap.get(publicKey) != null &&
					ReferencePeer.lastBlockMap.get(publicKey).equals(block.getHashMerkleRoot())) {
					ReferencePeer.lastBlockMap.put(publicKey, block.getHashMerkleRoot().toString());
					break;
				}
				for (Transaction tx : block.getTransactions()) {
					for (Output outs : tx.getOutputs()) {
						// Check whether the output belongs to the current
						// owner.
						if (outs.getLockingScript().equals(publicKey)) {
							ReferencePeer.availableAmount += outs.getAmount();
						}
					}	
				}
			}
		} catch (BlockNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ReferencePeer.availableAmount;
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
