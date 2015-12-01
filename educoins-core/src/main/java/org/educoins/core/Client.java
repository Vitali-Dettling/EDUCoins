package org.educoins.core;

import org.educoins.core.Input.EInputUnlockingScript;
import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Client extends Thread implements ITransactionListener {

	private BlockChain blockChain;
	private Wallet wallet;
	private List<Input> inputs;

	public Client(BlockChain blockChain) {
		this.setName("Client-Thread");
		this.blockChain = blockChain;
		this.blockChain.addTransactionListener(this);
		this.wallet = this.blockChain.getWallet();
		this.inputs = new ArrayList<>();
	}

	public Transaction sendRegularTransaction(int amount, String dstPublicKey, String lockingScript) {
		int availableAmount = 0;
		for (Input input : this.inputs) {
			availableAmount += input.getAmount();
		}
		if (amount > availableAmount) {
			System.err.println("Not enough available amount (max. " + availableAmount + ")");
			return null;
		}
		List<Output> outputs = new ArrayList<>();
		Output output = new Output(amount, dstPublicKey, lockingScript);
		outputs.add(output);
		if (amount < availableAmount) {
			int reverseOutputAmount = availableAmount - amount;
			String reverseDstPublicKey = this.wallet.getPublicKey();
			String reverseLockingScript = reverseDstPublicKey;
			Output reverseOutput = new Output(reverseOutputAmount, reverseDstPublicKey, reverseLockingScript);
			outputs.add(reverseOutput);
		}
		Transaction transaction = new Transaction();
		transaction.setVersion(1);
		transaction.setInputs(new ArrayList<>(this.inputs));
		transaction.setOutputs(outputs);

		List<Input> tmpInputs = new ArrayList<>(this.inputs);
		for (Input input : tmpInputs) {

			String publicKey = ByteArray
					.convertToString(input.getUnlockingScript(EInputUnlockingScript.PUBLIC_KEY), 16);
			String message = ByteArray.convertToString(transaction.hash(), 16);
			String signature = this.wallet.getSignature(publicKey, message);

			// TODO [joeren] @ [vitali]: hier muss ich die Signatur anhängen, da
			// brauch ich irgendwas, wie ich das UNFERTIG auslesen kann
			input.setUnlockingScript(EInputUnlockingScript.SIGNATURE, signature);

		}
		transaction.setInputs(inputs);
		this.blockChain.sendTransaction(transaction);
		this.inputs = new ArrayList<>();
		return transaction;
	}

	public Transaction sendApprovedTransaction(int amount, String owner, String holder, String lockingScript) {
		List<Input> tmpInputs = new ArrayList<>(this.inputs);
		this.inputs.removeAll(tmpInputs);

		int availableAmount = 0;
		for (Input input : tmpInputs) {
			availableAmount += input.getAmount();
			if (availableAmount >= amount) break;
			//TODO: maybe give back inputs here
		}
		if (amount > availableAmount) {
			System.err.println("Not enough available amount (max. " + availableAmount + ")");
			return null;
		}
		Approval approval = new Approval(amount, owner, holder, lockingScript);
		Output output = null;
		if (amount < availableAmount) {
			int reverseOutputAmount = availableAmount - amount;
			String reverseDstPublicKey = this.wallet.getPublicKey();
			output = new Output(reverseOutputAmount, reverseDstPublicKey, reverseDstPublicKey);
		}
		Transaction transaction = new Transaction();
		transaction.setVersion(1);
		transaction.setInputs(new ArrayList<>(tmpInputs));
		transaction.addApproval(approval);
		if (output != null) {
			transaction.addOutput(output);
		}

		for (Input input : tmpInputs) {

			String publicKey = ByteArray
					.convertToString(input.getUnlockingScript(EInputUnlockingScript.PUBLIC_KEY), 16);
			String message = ByteArray.convertToString(transaction.hash(), 16);
			String signature = this.wallet.getSignature(publicKey, message);

			// TODO [joeren] @ [vitali]: hier muss ich die Signatur anhängen, da
			// brauch ich irgendwas, wie ich das UNFERTIG auslesen kann
			input.setUnlockingScript(EInputUnlockingScript.SIGNATURE, signature);

		}
		transaction.setInputs(tmpInputs);
		this.blockChain.sendTransaction(transaction);
		return transaction;
	}

	public void revokeTransaction() {

	}

	@Override
	public void transactionReceived(Transaction transaction) {
		generateInputs(transaction);
	}

	private void generateInputs(Transaction transaction) {
		try {
			List<String> publicKeys = this.wallet.getPublicKeys();
			List<Output> availableOutputs = transaction.getOutputs();
			if (availableOutputs == null) {
				return;
			}
			for (int i = 0; i < availableOutputs.size(); i++) {
				Output output = availableOutputs.get(i);
				for (String publicKey : publicKeys) {
					if (publicKey.equals(output.getDstPublicKey())) {
						int amount = output.getAmount();
						String hashPrevOutput = ByteArray.convertToString(transaction.hash(), 16);
						// TODO [joeren] @ [vitali]: Wenn ich hier ";" bereits
						// anhänge, knallts bei irgendeinem Konvertiervorgang
						Input input = new Input(amount, hashPrevOutput, i);
						input.setUnlockingScript(EInputUnlockingScript.PUBLIC_KEY, this.wallet.getPublicKey());
						this.inputs.add(input);

						String typeString = null;
						ETransaction type = transaction.whichTransaction();
						if (type == ETransaction.COINBASE) {
							typeString = "Coinbase Transaction";
						} else if (type == ETransaction.REGULAR) {
							typeString = "Regular Transaction";
						} else if (type == ETransaction.APPROVED) {
							typeString = "Approved Transaction";
						} else {
							typeString = "Unknown Transaction";
						}
						int availableAmount = 0;
						List<Input> tmpInputs = new ArrayList<>(inputs);
						for (Input tmpInput : tmpInputs) {
							availableAmount += tmpInput.getAmount();
						}
						//TODO[Vitali] Testing
						//System.out.println(String.format("Info: Received %d EDUCoins (new Amount: %d) from a %s with LockingScript %s",
						//		amount, availableAmount, typeString, output.getLockingScript()));
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Cannot read public keys");
		}
	}

	@Override
	public void run() {
		boolean running = true;
		while (running) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action:");
			System.out.println("\t - (R)egular transaction");
			System.out.println("\t - (A)pproved transaction");
			System.out.println("\t - (X)Revoke transaction");
			System.out.println("\t - (B)reak client");
			String action = scanner.nextLine();
			int amount = -1;
			Transaction trans = null;
			switch (action.toLowerCase()) {
			case "r":
				amount = getIntInput(scanner, "Type in amount: ");
				if (amount == -1) continue;
				String dstPublicKey = getHexInput(scanner, "Type in dstPublicKey: ");
				if (dstPublicKey == null) continue;
				trans = this.sendRegularTransaction(amount, dstPublicKey, dstPublicKey);
				if (trans != null)
					System.out.println(Sha256Hash.wrap(trans.hash()));
				break;
			case "a":
				amount = getIntInput(scanner, "Type in amount: ");
				if (amount == -1) continue;
				System.out.print("Type in owner: ");
				String owner = scanner.nextLine();
				System.out.print("Type in holder: ");
				String holder = scanner.nextLine();
				System.out.print("Type in LockingScript: ");
				String lockingScript = scanner.nextLine();
				trans = this.sendApprovedTransaction(amount, owner, holder, lockingScript);
				if (trans != null)
					System.out.println(Sha256Hash.wrap(trans.hash()));
				break;
			case "x":

				break;
			case "b":
				running = false;
				break;
			default:
			}
		}
	}

	private int getIntInput(Scanner scanner, String prompt) {
		System.out.print(prompt);
		try {
			return Integer.valueOf(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Please enter a number value!");
			return -1;
		}
	}

	private String getHexInput(Scanner scanner, String prompt) {
		System.out.print(prompt);
		String input = scanner.nextLine();
		try {
			new BigInteger(input, 16);
		} catch (NumberFormatException e) {
			System.out.println("Please enter a valid hex value!");
			return null;
		}
		return input;
	}

}
