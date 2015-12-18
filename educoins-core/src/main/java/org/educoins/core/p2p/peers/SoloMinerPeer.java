package org.educoins.core.p2p.peers;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.IBlockListener;
import org.educoins.core.IPoWListener;
import org.educoins.core.ITransactionListener;
import org.educoins.core.ITransactionReceiver;
import org.educoins.core.Miner;
import org.educoins.core.Transaction;
import org.educoins.core.Wallet;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.utils.Sha256Hash;
import org.educoins.core.utils.Threading;

/**
 * The {@link Peer}-Type having only reading-capabilities. Created by typus on
 * 11/3/15.
 */
public class SoloMinerPeer extends Peer implements IPoWListener, ITransactionReceiver, ITransactionListener  {

	private Miner miner;
	// TODO only one public key will be used. Need to be improved in using
	// multiple keys.
	private static String singlePublicKey;
	


	public SoloMinerPeer() {
		super(new HttpProxyPeerGroup());
		
		Peer.wallet = new Wallet();
		Peer.client = new Client(wallet);
		Peer.blockChain = new BlockChain(wallet);
		this.miner = new Miner(Peer.blockChain);
	}

	@Override
	public void start() {
		
		miner.addPoWListener(this);
		miner.addPoWListener(Peer.remoteProxies);
		Peer.blockChain.addBlockListener(this);
		
		SoloMinerPeer.singlePublicKey = Peer.wallet.getPublicKey();
		// Kick off Miner.
		foundPoW(new Block());
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
				trans = Peer.client.sendRegularTransaction(amount, lockingScript);
				SoloMinerPeer.blockChain.sendTransaction(trans);
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
		public void foundPoW(Block block) {	
		
			logger.info("Found pow. (Block {})", block.hash().toString());
			Peer.blockChain.notifyBlockReceived(block);

			Threading.run(() -> Peer.blockListeners.forEach(iBlockListener -> iBlockListener.blockListener(block)));
			Block newBlock = Peer.blockChain.prepareNewBlock(block, singlePublicKey);
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
