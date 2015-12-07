package org.educoins.core;

import org.educoins.core.Input.EInputUnlockingScript;
import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.CannotRevokeRevokeTransactionException;
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
	private long lastFoundTime;

	public Client(BlockChain blockChain) {
		this.setName("Client-Thread");
		this.blockChain = blockChain;
		this.blockChain.addTransactionListener(this);
		this.wallet = this.blockChain.getWallet();
		this.inputs = new ArrayList<>();
		this.lastFoundTime = System.currentTimeMillis();
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
			String message = transaction.hash().toString();
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
		System.out.println(this.inputs.size());
		List<Input> tmpInputs = new ArrayList<>();
		int availableAmount = 0;

		for (int i = 0; availableAmount < amount && i < this.inputs.size(); i++) {
			tmpInputs.add(this.inputs.get(i));
			availableAmount += this.inputs.get(i).getAmount();
		}

		if (amount > availableAmount) {
			System.err.println("Not enough available amount (max. " + availableAmount + ")");
			return null;
		}
		this.inputs.removeAll(tmpInputs);

		Approval approval = new Approval(amount, owner, holder, lockingScript);
		Transaction transaction = new Transaction();
		transaction.setVersion(1);
		transaction.setInputs(new ArrayList<>(tmpInputs));
		transaction.addApproval(approval);

		if (amount < availableAmount) {
			int remainingAmount = availableAmount - amount;
			String senderPublicKey = this.wallet.getPublicKey();
			Output output = new Output(remainingAmount, senderPublicKey, senderPublicKey);
			transaction.addOutput(output);
		}

		String message = transaction.hash().toString();
		for (Input input : tmpInputs) {

			String publicKey = ByteArray.convertToString(input.getUnlockingScript(EInputUnlockingScript.PUBLIC_KEY), 16);
			String signature = this.wallet.getSignature(publicKey, message);

			// TODO [joeren] @ [vitali]: hier muss ich die Signatur anhängen, da
			// brauch ich irgendwas, wie ich das UNFERTIG auslesen kann
			input.setUnlockingScript(EInputUnlockingScript.SIGNATURE, signature);

		}
		this.blockChain.sendTransaction(transaction);
		return transaction;
	}

	public Transaction sendRevokeTransaction(Transaction transaction) {
		try {
			if (transaction == null) {
				System.out.println("Transaction not found");
				return null;
			}
			Transaction revoke = transaction.revokeTransaction();
			this.blockChain.sendTransaction(revoke);
			return revoke;
		} catch (CannotRevokeRevokeTransactionException e) {
			System.out.println("Cannot revoke a revokeTransaction");
			return null;
		}
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
						String hashPrevOutput = transaction.hash().toString();
						// TODO [joeren] @ [vitali]: Wenn ich hier ";" bereits
						// anhänge, knallts bei irgendeinem Konvertiervorgang
						Input input = new Input(amount, hashPrevOutput, i);
						input.setUnlockingScript(EInputUnlockingScript.PUBLIC_KEY, this.wallet.getPublicKey());
						this.inputs.add(input);

						String typeString;
						switch (transaction.whichTransaction()) {
							case APPROVED:
								typeString = "Approved Transaction";
								break;
							case COINBASE:
								typeString = "Coinbase Transaction";
								break;
							case REGULAR:
								typeString = "Regular Transaction";
								break;
							case REVOKE:
								typeString = "Revoke Transaction";
								break;
							default:
								typeString = "Unknown Transaction";
						}
						int availableAmount = 0;
						List<Input> tmpInputs = new ArrayList<>(inputs);
						for (Input tmpInput : tmpInputs) {
							availableAmount += tmpInput.getAmount();
						}
						//TODO[Vitali] Testing
						//System.out.println(String.format("Info:Received %d EDUCoins (new Amount: %d), time since last: % 6d ms. %s with LockingScript %s",
						//		amount, availableAmount,System.currentTimeMillis() - lastFoundTime,  typeString, output.getLockingScript()));
						lastFoundTime = System.currentTimeMillis();
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
					System.out.println(trans.hash());
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
				long time = System.currentTimeMillis();
				trans = this.sendApprovedTransaction(amount, owner, holder, lockingScript);
				System.out.println(System.currentTimeMillis() - time);
				if (trans != null)
					System.out.println(trans.hash());
				break;
			case "x":
				Sha256Hash hash = Sha256Hash.wrap(getHexInput(scanner, "Type in hash of transaction to revoke: "));
				trans = this.findTransaction(hash);
				Transaction revoke = this.sendRevokeTransaction(trans);
				if (revoke != null) {
					System.out.println("Revoked transaction: " + trans.hash());
					System.out.println("With Revoke: " + revoke.hash());
				}
				break;
			case "b":
				running = false;
				break;
			default:
			}
		}
	}

	private Transaction findTransaction(Sha256Hash hash) {
		return this.blockChain.getTransaction(hash);
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
		} catch (NullPointerException | NumberFormatException e) {
			System.out.println("Please enter a valid hex value!");
			return null;
		}
		return input;
	}

}
