package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.Sha256Hash;

import java.util.Scanner;

/**
 * The Reference Client consisting of a Miner, a {@link BlockChain} and a
 * {@link Wallet}. Created by typus on 11/23/15.
 */
public class ReferencePeer extends Peer {

    // TODO only one public key will be used. Need to be improved in using
    // multiple keys.
    protected final String singlePublicKey;
    protected final Client client;
    protected final Miner miner;

    public ReferencePeer(BlockChain blockChain, IProxyPeerGroup peerGroup, Sha256Hash publicKey) {
        super(blockChain, peerGroup, publicKey);
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
            System.out.println("Select action: ");
            System.out.println("\t - (P)Get Public Key");
            System.out.println("\t - (S)Create Signature");
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
                    System.out.println("Send to address: " + this.singlePublicKey);
                    break;
                case "s":
                    String hashTx = "123456789ABCDEF";
                    String signature = Wallet.getSignature(this.singlePublicKey, hashTx);
                    System.out.println("Created Signature: " + signature);
                case "g":
                    System.out.println("Regular EDUCoins " + this.client.getEDICoinsAmount());
                    System.out.println("Approved EDUCoins " + this.client.getApproveCoins());
                    break;
                case "r":
                    amount = this.client.getIntInput(scanner, "Type in amount: ");
                    int availableAmount = this.client.getEDICoinsAmount();
                    if (amount > availableAmount) {
                        System.err.println("Not enough available amount (max. " + availableAmount + ")");
                        break;
                    }
                    String dstPublicKey = this.client.getHexInput(scanner, "Type in dstPublicKey: ");
                    if (dstPublicKey == null)
                        continue;
                    trans = this.client.generateRegularTransaction(amount, dstPublicKey);
                    if (trans != null) {
                        this.blockChain.sendTransaction(trans);
                        System.out.println(trans.hash());
                    }
                    break;
                case "a":
                    amount = this.client.getIntInput(scanner, "Type in amount: ");
                    if (amount == -1)
                        continue;
                    System.out.print("Owner address is: " + this.singlePublicKey + "\n");
                    String owner = this.singlePublicKey;
                    System.out.print("Type in LockingScript: ");
                    String lockingScript = scanner.nextLine();
                    System.out.print("Holder signature is: ");
                    String holderSignature = scanner.nextLine();

                    trans = this.client.generateApprovedTransaction(amount, owner, holderSignature, lockingScript);
                    if (trans != null) {
                        this.blockChain.sendTransaction(trans);
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
                    trans = client.generateRevokeTransaction(hash, "");
                    if (trans != null) {
                        this.blockChain.sendTransaction(trans);
                        System.out.println("Revoked transaction: " + transToRevoke.hash());
                        System.out.println("With Revoke: " + trans.hash());
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
