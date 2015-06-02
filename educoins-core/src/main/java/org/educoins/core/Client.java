package org.educoins.core;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.educoins.core.utils.ByteArray;

public class Client extends Thread implements ITransactionListener {

	private BlockChain blockChain;
	private Wallet wallet;
	private List<Input> inputs;
	
	public Client(BlockChain blockChain, Wallet wallet) {
		this.blockChain = blockChain;
		//this.blockChain.addTransactionListener(this);
		this.wallet = wallet;
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
		transaction.setInputs(inputs);
		transaction.setOutputs(outputs);
		this.blockChain.sendTransaction(transaction);
		this.inputs.clear();
	}

	@Override
	public void transactionReceived(Transaction transaction) {
		generateInputs(transaction);
	}

	private void generateInputs(Transaction transaction) {
		List<String> publicKeys = new ArrayList<>(); //this.wallet.getPublicKeys();
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
					String unlockingScript = publicKey + ";";
					Input input = new Input(amount, hashPrevOutput, index, unlockingScript);
					this.inputs.add(input);
					System.out.println("Received " + amount);
				}
			}
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
				try {
					int amount = Integer.parseInt(scanner.nextLine());
					System.out.print("Type in dstPublicKey: ");
					String dstPublicKey = scanner.nextLine();
					String lockingScript = this.wallet.getPublicKey();
					System.out.println("Generated lockingScript: " + lockingScript);
					this.sendRegularTransaction(amount, dstPublicKey, lockingScript);
				} catch (NumberFormatException e) {
					System.err.println("Input was invalid");
				}
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client(null, new Wallet());
		client.start();
	}

}
