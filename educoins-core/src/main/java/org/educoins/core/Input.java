package org.educoins.core;

import org.educoins.core.utils.ByteArray;

public class Input {

	private int amount;
	private String hashPrevOutput;
	private String unlockingScript;
	private String signature;

	public Input(int amount, String hashPrevOutput, String unlockingScript) {

		this.amount = amount;
		this.hashPrevOutput = hashPrevOutput;
		this.unlockingScript = unlockingScript;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getHashPrevOutput() {
		return this.hashPrevOutput;
	}

	public void setHashPrevOutput(String hashPrevOutput) {
		this.hashPrevOutput = hashPrevOutput;
	}

	public String getUnlockingScript() {
		return this.unlockingScript;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSignature() {
		return this.signature;
	}

	public byte[] getConcatedInput() {

		// TODO May to concatenate more??? Did not Bitcoin say that
		// only the locking script is concatenated???
		return ByteArray.convertFromString(this.getUnlockingScript());

	}

	@Override
	public String toString() {
		return "Input [amount=" + amount + ", hashPrevOutput=" + hashPrevOutput + ", unlockingScript=" + unlockingScript
				+ ", signature=" + signature + "]";
	}

	

}
