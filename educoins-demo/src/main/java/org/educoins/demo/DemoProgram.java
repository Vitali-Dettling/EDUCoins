package org.educoins.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.stream.Stream;

import org.educoins.core.Block;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.IBlockTransmitter;
import org.educoins.core.Miner;
import org.educoins.core.Wallet;
import org.educoins.core.cryptography.ECDSA;

public class DemoProgram {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

		String localStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "localBlockChain";
		String remoteStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";	
		String walletStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "walletBlockChain";
		
		boolean localStorageSet = false;
		boolean remoteStorageSet = false;
		boolean walletStorageSet = false;
		
		boolean runMiner = false;
		boolean runWallet = false;
		boolean init = false;
		
		ECDSA ecdsa = new ECDSA();
		
		if (args.length != 0) {
	
			for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
				case "-localStorage":
					if (localStorageSet) {
						System.err.println("local storage can only set once");
						return;
					}
					localStorage = args[++i];
					localStorageSet = true;
					break;
				case "-remoteStorage":
					if (remoteStorageSet) {
						System.err.println("remote storage can only set once");
						return;
					}
					remoteStorage = args[++i];
					remoteStorageSet = true;
					break;
				case "-walletStorage":
					if (walletStorageSet) {
						System.err.println("wallet storage can only set once");
						return;
					}
					walletStorage = args[++i];
					walletStorageSet = true;
					break;
				case "-runMiner":
					if (runMiner) {
						System.err.println("runMiner can only set once");
						return;
					}
					runMiner = true;
					break;
				case "-runWallet":
					if (runWallet) {
						System.err.println("runWallet can only set once");
						return;
					}
					runWallet = true;
					break;
				case "-init":
					if (init) {
						System.err.println("init can only set once");
						;
						return;
					}
					init = true;
				default:
					System.err.println("illegal argument " + args[i]);
					return;
				}
			}

		} else {
			Scanner scanner = new Scanner(System.in);
			String input = null;
			
			//TODO [Vitali] Sollte durch eine GUI ersetzt werden, in dem man seinen Public key eintragen muss...
			System.out.print("EDUCoin Address: " + ecdsa.getPublicKey() + "\n");
			
			
			System.out.print("path of local storage (" + localStorage + "): ");
			input = scanner.nextLine().trim();
			if (!input.isEmpty()) {
				localStorage = input;
			}
			System.out.print("path of remote storage (" + remoteStorage + "): ");
			input = scanner.nextLine().trim();
			if (!input.isEmpty()) {
				remoteStorage = input;
			}
			System.out.print("path of wallet storage (" + walletStorage + "): ");
			input = scanner.nextLine().trim();
			if (!input.isEmpty()) {
				walletStorage = input;
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
			
			System.out.print("run wallet [Y|n]: ");
			input = scanner.nextLine().trim();
			if (input.isEmpty() || input.equalsIgnoreCase("y")) {
				runWallet = true;
			} else if (input.equalsIgnoreCase("n")) {
				runWallet = false;
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
			
			scanner.close();
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
			if (Files.exists(Paths.get(walletStorage))) {
				Stream<Path> walletFiles = Files.list(Paths.get(walletStorage));
				for (Object file : walletFiles.toArray()) {
					Files.delete((Path) file);
				}
				walletFiles.close();
			}
		}

		if (runWallet) {
			
			IBlockTransmitter blockTransmitterWallet = new DemoBlockTransmitterWallet(remoteStorage, walletStorage);
			
			IBlockReceiver blockReceiver = new DemoBlockReceiver(remoteStorage);
			
			Wallet wallet = new Wallet(blockReceiver, blockTransmitterWallet, ecdsa);

			blockReceiver.receiveBlocks();
		}
		
		if (runMiner) {
			IBlockTransmitter blockTransmitterMiner = new DemoBlockTransmitterMiner(localStorage, remoteStorage);
	
			IBlockReceiver blockReceiver = new DemoBlockReceiver(remoteStorage);

			Miner miner = new Miner(blockReceiver, blockTransmitterMiner, ecdsa);//TODO[Vitali] Braucht der Miner das ecdsa oder bekommt er es von der Wallet???
			
			blockReceiver.receiveBlocks();
			
			blockTransmitterMiner.transmitBlock(new Block());
		}
		
//		// Temporary
//		IBlockTransmitter blockTransmitter = new DemoBlockTransmitter(localStorage, remoteStorage);
//		IBlockReceiver blockReceiver = new DemoBlockReceiver(remoteStorage);
//		blockReceiver.receiveBlocks();
//		Block block = new Block();
//		ATransaction tx = new RegularTransaction();
//		block.addTransaction(tx);
//		blockTransmitter.transmitBlock(block);
	}

	
}
