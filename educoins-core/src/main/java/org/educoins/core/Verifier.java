package org.educoins.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Deserializer;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public abstract class Verifier {

	private static final int TRUE = 0;
	private static final String GENIUSES_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";

	public static boolean verifyBlock(Block testblock) {

		// 0. If geniuses block return true, because there no other block before.
		if (testblock.getHashPrevBlock().equals(GENIUSES_BLOCK)) {
			return true;
		}

		// 1. Find the previous block.
		Block lastBlock = getPreviousBlock(testblock);

		// 2. Does the previous block exist?
		if (lastBlock == null) {
			return false;
		}

		// 3. Are the hashes equal of the current block and the previous one?
		byte[] testBlockHash = testblock.hash();
		byte[] lastBlockHash = lastBlock.getHashPrevBlock().getBytes();
		if (ByteArray.compare(testBlockHash, lastBlockHash) == TRUE) {
			return false;
		}

		// TODO[Vitali] Überlegen ob weitere Test von nöten wären???

		return true;

	}

	public static boolean verifyTransaction(Transaction transaction) {

		// After "Bildungsnachweise als Digitale Währung - eine Anwendung der Block-Chain-Technologie" p. 37f

		// Case 1:
		// TODO [joeren]: Syntax has not to be verified in first step, already done by the deserializer

		// Case 2:
		// TODO [joeren]: implementation of approval-exception
		List<Input> inputs = transaction.getInputs();

		if (inputs == null) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: inputs is null");
			return false;
		}

		int realInputsCount = inputs.size();

		if (realInputsCount == 0) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: realInputsCount is 0");
			return false;
		}

		int inputsCount = transaction.getInputsCount();

		if (realInputsCount != inputsCount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: realInputsCount does not match inputsCount");
			return false;
		}

		List<Output> outputs = transaction.getOutputs();

		if (outputs == null) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: outputs is null");
			return false;
		}

		int realOutputsCount = outputs.size();

		if (realOutputsCount == 0) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: realOutputsCount is 0");
			return false;
		}

		int outputsCount = transaction.getOutputsCount();

		if (realOutputsCount != outputsCount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: realOutputsCount does not match outputsCount");
			return false;
		}
		
		int sumInputsAmount = 0;
		int sumOutputsAmount = 0;
		
		// Case 4:
		for (Input input : inputs) {
			int amount = input.getAmount();
			if (amount <= 0) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyTransaction: input amounts is negative or zero");
				return false;
			}
			// sum up for case 5
			sumInputsAmount += amount;
		}
		for (Output output : outputs) {
			int amount = output.getAmount();
			if (amount <= 0) {
				// TODO [joeren]: remove debug output
				System.out.println("DEBUG: verifyTransaction: output amount is negative or zero");
				return false;
			}
			// sum up for case 5
			sumOutputsAmount += amount;
		}
		
		// Case 5:
		// TODO [joeren]: implementation of approval-exception
		if (sumOutputsAmount > sumInputsAmount) {
			// TODO [joeren]: remove debug output
			System.out.println("DEBUG: verifyTransaction: more output than input");
			return false;
		}

		return true;

	}

	private static Block getPreviousBlock(Block testblock) {
		try {

			String lastBlockName = testblock.getHashPrevBlock();

			// TODO[Vitali] Der remoteStorage String ist nur für den Prototypen, sollte geändert werden sobal eine
			// levelDB eingeführt wird!!!
			String remoteStoragePath = System.getProperty("user.home") + File.separator + "documents" + File.separator
					+ "educoins" + File.separator + "demo" + File.separator + "remoteBlockChain";

			return Deserializer.deserialize(remoteStoragePath, lastBlockName);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			System.out.println("ERROR: Class Verifier: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

}
