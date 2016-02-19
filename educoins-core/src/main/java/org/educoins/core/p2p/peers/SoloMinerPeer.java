package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.transaction.Transaction.ETransaction;
import org.educoins.core.utils.Threading;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * The {@link Peer}-Type having only reading-capabilities. Created by typus on
 * 11/3/15.
 */
public class SoloMinerPeer extends Peer implements IPoWListener, ITransactionReceiver, ITransactionListener {

	// TODO only one public key will be used. Need to be improved in using
	// multiple keys.
	private static String singlePublicKey;
	private Miner miner;

	public SoloMinerPeer(BlockChain blockChain) {
		super(blockChain);
		this.miner = new Miner(Peer.blockChain);
		SoloMinerPeer.singlePublicKey = Wallet.getPublicKey();
	}
	
	public String getPublicKey(){
		return SoloMinerPeer.singlePublicKey;
	}

	@Override
	public void start() {
		miner.addPoWListener(this);
			
		// Kick off Miner.
		foundPoW(blockChain.getLatestBlock());
		// After miner has started.
		Peer.remoteProxies.discover();
		client();
	}

	@Override
	public void stop() {
		miner.removePoWListener(this);
		miner.removePoWListener(Peer.remoteProxies);
		Peer.blockChain.removeBlockListener(this);
	}

	@Override
	protected void handleNewValidBlock(Block block) {
		//TODO ??? If committed in the miner is going crazy.
//		Block newBlock = blockChain.prepareNewBlock(block, singlePublicKey);
//		miner.receiveBlocks(newBlock);
	}


	private void client() {

		boolean running = true;
		while (running) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action:");
			System.out.println("\t - (G)Get own EDUCoins");
			System.out.println("\t - (R)egular transaction");
			System.out.println("\t - (L)ist of Transactions");
			System.out.println("\t - (E)xit");
			String action = scanner.nextLine();
			Transaction trans = null;
			int amount = -1;
			switch (action.toLowerCase()) {
			case "g":
				System.out.println("Regular EDUCoins " + Peer.client.getRegularAmount());
				break;
			case "r":
				amount = Peer.client.getIntInput(scanner, "Type in amount: ");
				if (amount == -1)
					continue;
				String lockingScript = Peer.client.getHexInput(scanner, "Type in dstPublicKey: ");
				if (lockingScript == null)
					continue;
				trans = Peer.client.generateRegularTransaction(amount, lockingScript);
				if (trans != null) {
					Peer.blockChain.sendTransaction(trans);
					System.out.println(trans.hash());
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
			case "e":
				running = false;
				break;
			default:
			}
		}
	}

	// region listeners

	@Override
	public void foundPoW(Block powBlock) {
		logger.info("Found pow. (Block {})", powBlock.hash().toString());
		Peer.blockChain.notifyBlockReceived(powBlock);
		Threading.run(() -> remoteProxies.foundPoW(powBlock));
		//New round of miner.
		Block newBlock = Peer.blockChain.prepareNewBlock(powBlock, singlePublicKey);
		this.miner.receiveBlocks(newBlock);
	}

	@Override
	public void transactionReceived(Transaction transaction) {
		Peer.blockChain.transactionReceived(transaction);
	}

	@Override
	public void addTransactionListener(ITransactionListener transactionListener) {
		Peer.remoteProxies.addTransactionListener(transactionListener);
	}

	@Override
	public void removeTransactionListener(ITransactionListener transactionListener) {
		Peer.remoteProxies.removeTransactionListener(transactionListener);
	}

	@Override
	public void receiveTransactions() {
		remoteProxies.receiveTransactions();
	}

	// endregion

}
