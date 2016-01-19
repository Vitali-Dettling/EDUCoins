package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.Sha256Hash;

import java.util.Scanner;

/**
 * The {@link Peer}-Type having only reading-capabilities. Created by typus on
 * 11/3/15.
 */
public class SoloMinerPeer extends Peer {
    protected final String singlePublicKey;
    protected final Client client;
    protected final Miner miner;

    public SoloMinerPeer(BlockChain blockChain, IProxyPeerGroup proxyPeerGroup, Sha256Hash publicKey) {
        super(blockChain, proxyPeerGroup, publicKey);
        this.miner = new Miner(blockChain);
        this.client = new Client();
        this.singlePublicKey = Wallet.getPublicKey();

        this.blockChain.addBlockListener(client);
    }

    @Override
    public void start() throws DiscoveryException {
        super.start();
        //kick off miner

        blockChain.foundPoW(blockChain.getLatestBlock());
        client();
    }

    @Override
    public void stop() {
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
                    System.out.println("Regular EDUCoins " + this.client.getEDICoinsAmount());
                    break;
                case "r":
                    amount = this.client.getIntInput(scanner, "Type in amount: ");
                    if (amount == -1)
                        continue;
                    String lockingScript = this.client.getHexInput(scanner, "Type in dstPublicKey: ");
                    if (lockingScript == null)
                        continue;
                    trans = this.client.generateRegularTransaction(amount, lockingScript);
                    if (trans != null) {
                        this.blockChain.sendTransaction(trans);
                        System.out.println(trans.hash());
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
