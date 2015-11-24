package org.educoins.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.Input.EInputUnlockingScript;
import org.educoins.core.Transaction.ETransaction;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;

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
		Output output = new Output(amount, Sha256Hash.wrap(dstPublicKey), Sha256Hash.wrap(lockingScript));
		outputs.add(output);
		if (amount < availableAmount) {
			int reverseOutputAmount = availableAmount - amount;
			String reverseDstPublicKey = this.wallet.getPublicKey();
			String reverseLockingScript = reverseDstPublicKey;
			Output reverseOutput = new Output(reverseOutputAmount, Sha256Hash.wrap(reverseDstPublicKey), Sha256Hash.wrap(reverseLockingScript));
			outputs.add(reverseOutput);
		}
		Transaction transaction = new Transaction();
		transaction.setVersion(1);
		transaction.setInputs(new ArrayList<>(this.inputs));
		transaction.setOutputs(outputs);

		for (Input input : this.inputs) {

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
			output = new Output(reverseOutputAmount, Sha256Hash.wrap(reverseDstPublicKey), Sha256Hash.wrap(reverseLockingScript));
		}
		Transaction transaction = new Transaction();
		transaction.setVersion(1);
		transaction.setInputs(new ArrayList<>(this.inputs));
		transaction.addApproval(approval);
		if (output != null) {
			transaction.addOutput(output);
		}

		for (Input input : this.inputs) {

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
						Input input = new Input(amount, Sha256Hash.wrap(hashPrevOutput), index);
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
						System.out.println(String.format("Info: Received %d EDUCoins (new Amount: %d) from a %s with LockingScript %s",
								amount, availableAmount, typeString, output.getLockingScript()));
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
			default:
			}
		}
	}

}
