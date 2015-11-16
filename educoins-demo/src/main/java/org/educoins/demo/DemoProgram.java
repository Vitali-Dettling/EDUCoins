package org.educoins.demo;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.Client;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.ITransactionListener;
import org.educoins.core.ITransactionReceiver;
import org.educoins.core.ITransactionTransmitter;
import org.educoins.core.p2p.P2pBlockReceiver;
import org.educoins.core.p2p.discovery.LocalDiscovery;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.miner.Miner;

public class DemoProgram {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        String localDBStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
                + "educoins" + File.separator + "demo" + File.separator + "BlockChain" + File.separator + "blockstore" ;
        
        boolean localStorageSet = false;
        boolean runMiner = false;
        boolean init = false;

        if (args.length != 0) {

			for (int i = 0; i < args.length; i++) {
				switch (args[i].toLowerCase()) {
				case "-localstorage":
					if (localStorageSet) {
						System.err.println("local storage can only set once");
						return;
					}
					localDBStorage = args[++i];
					localStorageSet = true;
					break;
				case "-runminer":
					if (runMiner) {
						System.err.println("runMiner can only set once");
						return;
					}
					runMiner = true;
					break;
				case "-init":
					if (init) {
						System.err.println("init can only set once");
						return;
					}
					init = true;
					break;
				default:
					System.err.println("illegal argument " + args[i]);
					return;
				}
			}

        } else {
            Scanner scanner = new Scanner(System.in);
            String input = null;

            System.out.print("path of local storage (" + localDBStorage + "): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
            	localDBStorage = input;
            }
            System.out.print("run miner [Y|n]: ");
            input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equalsIgnoreCase("y")) {
                runMiner = true;
            } else if (input.equalsIgnoreCase("n")) {
                runMiner = false;
            } else {
                System.err.println("illegal input " + input);
                scanner.close();
                return;
            }

            System.out.print("initial run [Y|n]: ");
            input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equalsIgnoreCase("y")) {
                init = true;
            } else if (input.equalsIgnoreCase("n")) {
                init = false;
            } else {
                System.err.println("illegal input " + input);
                scanner.close();
                return;
            }
        }

        // make little space between input and run
        System.out.println();
        
        IBlockStore senderBlockStore = new LevelDbBlockStore(new File(localDBStorage));

        IBlockReceiver blockReceiver = new DemoBlockReceiver(senderBlockStore);
        blockReceiver.addBlockListener(senderBlockStore::put);

        IBlockReceiver p2pBlockReceiver =
                		new P2pBlockReceiver(
                        new LevelDbBlockStore(new File(localDBStorage)),
                        new LocalDiscovery(senderBlockStore));

        ITransactionReceiver txReceiver = new DemoTransactionReceiver();
        ITransactionTransmitter txTransmitter = new DemoTransactionTransmitter((ITransactionListener) txReceiver);
        
        BlockChain blockChain = new BlockChain(blockReceiver, txReceiver, txTransmitter, senderBlockStore);

        if (runMiner) {
            new Miner(blockChain);
        }
        Thread client = new Client(blockChain);
        client.start();

        //Kick of the system with the genesis block. 
        blockChain.foundPoW(new Block());
        p2pBlockReceiver.receiveBlocks();
        txReceiver.receiveTransactions();
    }
}
