package org.educoins.core;

public class Input {

	private int amount;
	private String hashPrevOutput;
	private String srcPublicKey;
	private String unlockingScript;

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

	public String getSrcPublicKey() {
		return this.srcPublicKey;
	}

	public void setSrcPublicKey(String publicKey) {
		this.srcPublicKey = publicKey;
	}

	public String getUnlockingScript() {
		return this.unlockingScript;
	}

	public void setUnlockingScript(String unlockingScript) {
		this.unlockingScript = unlockingScript;
	}

}
