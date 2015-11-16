package org.educoins.demo;

import org.educoins.core.*;
import org.educoins.core.p2p.P2pBlockReceiver;
import org.educoins.core.p2p.discovery.LocalDiscovery;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.miner.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.stream.Stream;


public class DemoProgram {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        String localStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
                + "educoins" + File.separator + "demo" + File.separator + "localBlockChain";
        String remoteStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
                + "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";

        boolean localStorageSet = false;
        boolean remoteStorageSet = false;

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
					localStorage = args[++i];
					localStorageSet = true;
					break;
				case "-remotestorage":
					if (remoteStorageSet) {
						System.err.println("remote storage can only set once");
						return;
					}
					remoteStorage = args[++i];
					remoteStorageSet = true;
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

            System.out.print("path of local storage (" + localStorage + "): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                localStorage = input;
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

        if (init) {
            if (Files.exists(Paths.get(localStorage))) {
                Stream<Path> localFiles = Files.list(Paths.get(localStorage));
                for (Object file : localFiles.toArray()) {
                    Files.delete((Path) file);
                }
                localFiles.close();
            }
            if (Files.exists(Paths.get(remoteStorage))) {
                Stream<Path> remoteFiles = Files.list(Paths.get(remoteStorage));
                for (Object file : remoteFiles.toArray()) {
                    Files.delete((Path) file);
                }
                remoteFiles.close();
            }
        }


        //region P2pBlockReceiver
        IBlockTransmitter blockTransmitter = new DemoBlockTransmitter(localStorage, remoteStorage);

        LevelDbBlockStore senderBlockStore = new LevelDbBlockStore(new File("/tmp/senderBlocks"));

        IBlockReceiver blockReceiver = new DemoBlockReceiver(remoteStorage);
        blockReceiver.addBlockListener(senderBlockStore::put);

        IBlockReceiver p2pBlockReceiver =
                new P2pBlockReceiver(
                        new LevelDbBlockStore(new File("/tmp/receiverBlocks")),
                        new LocalDiscovery(senderBlockStore));

        ITransactionReceiver txReceiver = new DemoTransactionReceiver();
        ITransactionTransmitter txTransmitter = new DemoTransactionTransmitter((ITransactionListener) txReceiver);

        BlockChain blockChain = new BlockChain(blockReceiver, blockTransmitter, txReceiver, txTransmitter);

        if (runMiner) {
            new Miner(blockChain);
        }
        Thread client = new Client(blockChain);
        client.start();

        blockReceiver.receiveBlocks();
        p2pBlockReceiver.receiveBlocks();
        //endregion

        txReceiver.receiveTransactions();
        Block block = new Block();
        block.addTransaction(coinbaseTransaction());
        blockTransmitter.transmitBlock(new Block());

        // // Temporary
        // IBlockTransmitter blockTransmitter = new DemoBlockTransmitter(localStorage, remoteStorage);
        // IBlockReceiver blockReceiver = new DemoBlockReceiver(remoteStorage);
        // blockReceiver.receiveBlocks();
        // Block block = new Block();
        // ATransaction tx = new RegularTransaction();
        // block.addTransaction(tx);
        // blockTransmitter.transmitBlock(block);
    }

    // TODO [Vitali] Delete
    private static Transaction coinbaseTransaction() {

        String burnedBublicKey = "00000000000000000000000000000000000000000000";

        // TODO [Vitali] lockingScript procedure has to be established, which fits our needs...
        String lockingScript = burnedBublicKey;// TODO[Vitali] Modify that it can be changed on or more addresses???

        // Input is empty because it is a coinbase transaction.
        Output output = new Output(10, burnedBublicKey, lockingScript);

        RegularTransaction transaction = new RegularTransaction();
        transaction.addOutput(output);
        return transaction;
    }

}
