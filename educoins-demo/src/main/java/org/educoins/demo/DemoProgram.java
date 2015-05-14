package org.educoins.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

import org.educoins.core.IBlockTransmitter;

public class DemoProgram {

	public static void main(String[] args) throws IOException {

		String localStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "localBlockChain";
		boolean localStorageSet = false;
		String remoteStorage = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";
		boolean remoteStorageSet = false;

		boolean runMiner = false;
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
			Stream<Path> localFiles = Files.list(Paths.get(localStorage));
			for (Object file : localFiles.toArray()) {
				Files.delete((Path)file);
			}
			localFiles.close();
			Stream<Path> remoteFiles = Files.list(Paths.get(remoteStorage));
			for (Object file : remoteFiles.toArray()) {
				Files.delete((Path)file);
			}
			remoteFiles.close();
		}

		if (runMiner) {
			IBlockTransmitter blockTransmitter = new DemoBlockTransmitter(localStorage, remoteStorage);
		}
	}
}
