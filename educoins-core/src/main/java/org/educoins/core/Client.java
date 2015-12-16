package org.educoins.core;

import org.educoins.core.Input.EInputUnlockingScript;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.p2p.peers.ReferencePeer;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.CannotRevokeRevokeTransactionException;
import org.educoins.core.utils.Sha256Hash;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client implements ITransactionListener {

	private BlockChain blockChain;
	private Wallet wallet;
	private List<Input> inputs;
	private long lastFoundTime;
	private IBlockStore store;

	private static int availableAmount = 0;
	private static Block latestSearchedBlock;

	public Client(BlockChain blockChain) {
		this.blockChain = blockChain;
		this.blockChain.addTransactionListener(this);
		this.wallet = this.blockChain.getWallet();
		this.inputs = new ArrayList<>();
		this.lastFoundTime = System.currentTimeMillis();
		this.store = this.blockChain.getBlockStore();
		Client.latestSearchedBlock = new Block();
	}

	public Transaction sendRegularTransaction(int amount, String dstPublicKey, String lockingScript) {

		if (amount > Client.availableAmount) {
			System.err.println("Not enough available amount");
			return null;
		}
		
		List<Output> outputs = new ArrayList<>();
		if (amount < Client.availableAmount) {
			//Send to address.
			Output output = new Output(amount, lockingScript, lockingScript);
			outputs.add(output);
			//Getting back.
			int reverseOutputAmount = Client.availableAmount - amount;
			String reverseDstPublicKey = dstPublicKey;
			String reverseLockingScript = dstPublicKey;
			Output reverseOutput = new Output(reverseOutputAmount, reverseDstPublicKey, reverseLockingScript);
			outputs.add(reverseOutput);
			Client.availableAmount = 0;
		}

		Transaction transaction = new Transaction();
		transaction.setVersion(1);
		transaction.setInputs(new ArrayList<>(this.inputs));
		transaction.setOutputs(outputs);

		List<Input> tmpInputs = new ArrayList<>(this.inputs);
		for (Input input : tmpInputs) {

			String publicKey = ByteArray.convertToString(input.getUnlockingScript(EInputUnlockingScript.PUBLIC_KEY),
					16);
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

			String publicKey = ByteArray.convertToString(input.getUnlockingScript(EInputUnlockingScript.PUBLIC_KEY),
					16);
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
					// TODO[Vitali] Testing
					// System.out.println(String.format("Info:Received %d
					// EDUCoins (new Amount: %d), time since last: % 6d ms.
					// %s with LockingScript %s",
					// amount, availableAmount,System.currentTimeMillis() -
					// lastFoundTime, typeString,
					// output.getLockingScript()));
					lastFoundTime = System.currentTimeMillis();
				}
			}
		}
	}

	public Transaction findTransaction(Sha256Hash hash) {
		return this.blockChain.getTransaction(hash);
	}

	public int getIntInput(Scanner scanner, String prompt) {
		System.out.print(prompt);
		try {
			return Integer.valueOf(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Please enter a number value!");
			return -1;
		}
	}

	public String getHexInput(Scanner scanner, String prompt) {
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

	public int getAmount(List<String> publicKeys) {

		IBlockIterator iterator = this.store.iterator();

		try {
			while (iterator.hasNext()) {
				Block block = iterator.next();
				// Break up as soon as the last searched block was found.
				if (Client.latestSearchedBlock.equals(block)) {
					break;
				}
				for (Transaction tx : block.getTransactions()) {
					for (Output outs : tx.getOutputs()) {
						// Check whether the output belongs to the current
						// owner.
						for (String publicKey : publicKeys) {
							if (outs.getLockingScript().equals(publicKey)) {
								Client.availableAmount += outs.getAmount();
							}
						}
					}
				}
			}
			Client.latestSearchedBlock = this.store.getLatest();
		} catch (BlockNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Client.availableAmount;
	}
}
