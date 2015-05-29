package org.educoins.core;

public class Approval {

	private int amount;
	private String hashPrevOutput;
	private String holderSignature;
	private String ownerAddress;
	private String lockingScript;
	
	public Approval(int amount, String hashPrevOutput, String ownerAddress, String holderSignature, String lockingScript){
		
		this.amount = amount;
		this.hashPrevOutput = hashPrevOutput;
		this.holderSignature = holderSignature;
		this.ownerAddress = ownerAddress;
		this.lockingScript = lockingScript;
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

	public String getHolderSignature() {
		return this.holderSignature;
	}

	public void setHolderSignature(String holderSignature) {
		this.holderSignature = holderSignature;
	}

	public String getOwnerAddress() {
		return ownerAddress;
	}

	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}
	
	public String getLockingScript() {
		return this.lockingScript;
	}

	public void setLockingScript(String lockingScript) {
		this.lockingScript = lockingScript;
	}
	
	
	
	
	
	
	
}
