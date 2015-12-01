package org.educoins.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.Input.EInputUnlockingScript;
import org.educoins.core.utils.ByteArray;

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

	public void sendRegularTransaction(int amount, String dstPublicKey, String lockingScript) {
		int availableAmount = 0;
		for (Input input : this.inputs) {
			availableAmount += input.getAmount();
		}
		if (amount > availableAmount) {
			System.err.println("Not enough available amount (max. " + availableAmount + ")");
			return;
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

		for (Input input : this.inputs) {

			String publicKey = ByteArray.convertToString(input.getUnlockingScript(EInputUnlockingScript.PUBLIC_KEY),
					16);
			String message = ByteArray.convertToString(transaction.hash(), 16);
			String signature = this.wallet.getSignature(publicKey, message);

			// TODO [joeren] @ [vitali]: hier muss ich die Signatur anhängen, da
			// brauch ich irgendwas, wie ich das UNFERTIG auslesen kann
			input.setUnlockingScript(EInputUnlockingScript.SIGNATURE, signature);

		}
		transaction.setInputs(inputs);
		this.blockChain.sendTransaction(transaction);
		this.inputs = new ArrayList<>();
	}

	public void sendApprovedTransaction(int amount, String owner, String holder, String lockingScript) {
		int availableAmount = 0;
		for (Input input : this.inputs) {
			availableAmount += input.getAmount();
		}
		if (amount > availableAmount) {
			System.err.println("Not enough available amount (max. " + availableAmount + ")");
			return;
		}
		Approval approval = new Approval(amount, owner, holder, lockingScript);
		Output output = null;
		if (amount < availableAmount) {
			int reverseOutputAmount = availableAmount - amount;
			String reverseDstPublicKey = this.wallet.getPublicKey();
			String reverseLockingScript = reverseDstPublicKey;
			output = new Output(reverseOutputAmount, reverseDstPublicKey, reverseLockingScript);
		}
		Transaction transaction = new Transaction();
		transaction.setVersion(1);
		transaction.setInputs(new ArrayList<>(this.inputs));
		transaction.addApproval(approval);
		if (output != null) {
			transaction.addOutput(output);
		}

		for (Input input : this.inputs) {

			String publicKey = ByteArray.convertToString(input.getUnlockingScript(EInputUnlockingScript.PUBLIC_KEY),
					16);
			String message = ByteArray.convertToString(transaction.hash(), 16);
			String signature = this.wallet.getSignature(publicKey, message);

			// TODO [joeren] @ [vitali]: hier muss ich die Signatur anhängen, da
			// brauch ich irgendwas, wie ich das UNFERTIG auslesen kann
			input.setUnlockingScript(EInputUnlockingScript.SIGNATURE, signature);

		}
		transaction.setInputs(inputs);
		this.blockChain.sendTransaction(transaction);
		this.inputs = new ArrayList<>();
	}

	public void sendGateTransaction(String publicKey) {
		Transaction transaction = new Transaction();
		transaction.setVersion(1);

		Gate gate = new Gate();
		transaction.setGate(gate);
		gate.setPublicKey(publicKey);

		byte[] hash = transaction.hash();
		String hashedTranscation = ByteArray.convertToString(hash, 16);

		String signature = this.wallet.getSignature(publicKey, hashedTranscation);
		gate.setSignature(signature);

		transaction.setGate(gate);
		this.blockChain.sendTransaction(transaction);
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
						int index = i;
						int amount = output.getAmount();
						String hashPrevOutput = ByteArray.convertToString(transaction.hash(), 16);
						// TODO [joeren] @ [vitali]: Wenn ich hier ";" bereits
						// anhänge, knallts bei irgendeinem Konvertiervorgang
						Input input = new Input(amount, hashPrevOutput, index);
						input.setUnlockingScript(EInputUnlockingScript.PUBLIC_KEY, this.wallet.getPublicKey());
						this.inputs.add(input);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Cannot read public keys");
		}
	}

	@Override
	public void run() {
		while (true) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Select action:");
			System.out.println("\t - (R)egular transaction");
			System.out.println("\t - (A)pproved transaction");
			System.out.println("\t - (G)ateway transaction");
			String action = scanner.nextLine();
			String unparsedAmount = null;
			int amount = -1;
			switch (action.toLowerCase()) {
			case "r":
				System.out.print("Type in amount: ");
				unparsedAmount = scanner.nextLine();
				amount = Integer.valueOf(unparsedAmount);
				System.out.print("Type in dstPublicKey: ");
				String dstPublicKey = scanner.nextLine();
				this.sendRegularTransaction(amount, dstPublicKey, dstPublicKey);
				break;
			case "a":
				System.out.print("Type in amount: ");
				unparsedAmount = scanner.nextLine();
				amount = Integer.valueOf(unparsedAmount);
				System.out.print("Type in owner: ");
				String owner = scanner.nextLine();
				System.out.print("Type in holder: ");
				String holder = scanner.nextLine();
				System.out.print("Type in LockingScript: ");
				String lockingScript = scanner.nextLine();
				this.sendApprovedTransaction(amount, owner, holder, lockingScript);
				break;
			case "g":
				System.out.print("Gate is broadcasted.");
				System.out.print("Type in your public key: ");
				String publicKey = scanner.nextLine();
				this.sendGateTransaction(publicKey);
				break;
			default:
			}
		}
	}

}
