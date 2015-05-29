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
import org.educoins.core.BlockChain;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.IBlockTransmitter;
import org.educoins.core.Miner;
import org.educoins.core.Wallet;

public class DemoProgram {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

		String localStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "localBlockChain";
		String remoteStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";	
		
		boolean localStorageSet = false;
		boolean remoteStorageSet = false;
		
		boolean runMiner = false;
		boolean runWallet = false;
		boolean init = false;
		
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
		}

		IBlockTransmitter blockTransmitter = new DemoBlockTransmitter(localStorage, remoteStorage);
		IBlockReceiver blockReceiver = new DemoBlockReceiver(remoteStorage);
		
		Wallet wallet = null;
		BlockChain blockChain = new BlockChain(blockReceiver, blockTransmitter);
		
		if (runWallet) {		
			wallet = new Wallet(blockChain);
		}
		
		if (runMiner) {
			new Miner(blockChain, wallet);
		}
	
		blockReceiver.receiveBlocks();
		blockTransmitter.transmitBlock(new Block());
		
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
