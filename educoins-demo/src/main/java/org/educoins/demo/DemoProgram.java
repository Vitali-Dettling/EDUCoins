package org.educoins.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.stream.Stream;

import org.educoins.core.ATransaction;
import org.educoins.core.Block;
import org.educoins.core.IBlockReceiver;
import org.educoins.core.IBlockTransmitter;
import org.educoins.core.Miner;
import org.educoins.core.RegularTransaction;
import org.educoins.core.cryptography.ECDSA;
import org.educoins.core.cryptography.SHA256Hasher;

public class DemoProgram {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

		String localStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "localBlockChain";
		boolean localStorageSet = false;
		String remoteStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";
		boolean remoteStorageSet = false;

		boolean runMiner = false;
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
				case "-runMiner":
					if (runMiner) {
						System.err.println("runMiner can only set once");
						return;
					}
					runMiner = true;
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
			if (Files.exists(Paths.get(localStorage))) {
				Stream<Path> remoteFiles = Files.list(Paths.get(remoteStorage));
				for (Object file : remoteFiles.toArray()) {
					Files.delete((Path) file);
				}
				remoteFiles.close();
			}
		}

		if (runMiner) {
			IBlockTransmitter blockTransmitter = new DemoBlockTransmitter(localStorage, remoteStorage);
			IBlockReceiver blockReceiver = new DemoBlockReceiver(remoteStorage);
			blockReceiver.receiveBlocks();
			Miner miner = new Miner(blockReceiver, blockTransmitter, new SHA256Hasher(), ecdsa);
			Block block = new Block();
			blockTransmitter.transmitBlock(block);
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
