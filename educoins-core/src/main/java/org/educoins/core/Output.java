package org.educoins.core;

public class Output {

	private int amount;
	private String dstPublicKey;
	private String lockingScript;

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getDstPublicKey() {
		return this.dstPublicKey;
	}

	public void setDstPublicKey(String dstPublicKey) {
		this.dstPublicKey = dstPublicKey;
	}

	public String getLockingScript() {
		return this.lockingScript;
	}

	public void setLockingScript(String lockingScript) {
		this.lockingScript = lockingScript;
	}

}
