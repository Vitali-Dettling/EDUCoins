package org.educoins.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.Input.EInputUnlockingScriptSeperator;
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
		String signature = this.wallet.getSignature(ByteArray.convertToString(transaction.hash(), 16));
		for (Input input : this.inputs) {
			// TODO [joeren] @ [vitali]: hier muss ich die Signatur anhängen, da brauch ich irgendwas, wie ich das UNFERTIG auslesen kann
			//input.setUnlockingScript(input.getRawUnlockingScript() + ";" + signature);
		}
		transaction.setInputs(inputs);
		this.blockChain.sendTransaction(transaction);
		this.inputs.clear();
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
						// TODO [joeren] @ [vitali]: Wenn ich hier ";" bereits anhänge, knallts bei irgendeinem Konvertiervorgang
						// String unlockingScript = publicKey + ";";
						String unlockingScript = publicKey;
						Input input = new Input(amount, hashPrevOutput, index, unlockingScript);
						this.inputs.add(input);
						// System.out.println("Received " + amount);
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
			String action = scanner.nextLine();
			switch (action.toLowerCase()) {
			case "r":
				System.out.print("Type in amount: ");
				String unparsedAmount = scanner.nextLine();
				int amount = Integer.valueOf(unparsedAmount);
				System.out.print("Type in dstPublicKey: ");
				String dstPublicKey = scanner.nextLine();
				String lockingScript = this.wallet.getPublicKey();
				System.out.println("Generated lockingScript: " + lockingScript);
				this.sendRegularTransaction(amount, dstPublicKey, lockingScript);
				System.err.println("Input was invalid: " + unparsedAmount);
				break;
			default:
			}
		}
	}

}
