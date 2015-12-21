package org.educoins.core.p2p.peers;

import java.util.Scanner;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.IPoWListener;
import org.educoins.core.ITransactionListener;
import org.educoins.core.ITransactionReceiver;
import org.educoins.core.Miner;
import org.educoins.core.Transaction;
import org.educoins.core.Wallet;
import org.educoins.core.utils.Threading;

/**
 * The {@link Peer}-Type having only reading-capabilities. Created by typus on
 * 11/3/15.
 */
public class SoloMinerPeer extends Peer implements IPoWListener, ITransactionReceiver, ITransactionListener {

	private Miner miner;
	// TODO only one public key will be used. Need to be improved in using
	// multiple keys.
	private static String singlePublicKey;

	public SoloMinerPeer(BlockChain blockChain) {
		super(blockChain);
		this.miner = new Miner(Peer.blockChain);
	}

	@Override
	public void start() {

		miner.addPoWListener(this);
		miner.addPoWListener(Peer.remoteProxies);

		SoloMinerPeer.singlePublicKey = Peer.wallet.getPublicKey();
		// Kick off Miner.
		foundPoW(new Block());
		// After miner has started.
		Peer.remoteProxies.discover();
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
			Transaction trans = null;
			int amount = -1;
			switch (action.toLowerCase()) {
			case "g":
				System.out.println("Owen EDUCoins " + Peer.client.getAmount());
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
				}
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
		miner.removePoWListener(this);
		miner.removePoWListener(Peer.remoteProxies);
		Peer.blockChain.removeBlockListener(this);
	}

	// region listeners

	@Override
	public void foundPoW(Block powBlock) {

		logger.info("Found pow. (Block {})", powBlock.hash().toString());
		Peer.blockChain.notifyBlockReceived(powBlock);
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
