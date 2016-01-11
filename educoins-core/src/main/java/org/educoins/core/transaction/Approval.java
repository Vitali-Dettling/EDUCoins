package org.educoins.core.transaction;

import org.educoins.core.utils.ByteArray;

public class Approval {

	private int amount;
	private String holderSignature;
	private String ownerAddress;
	private String lockingScript;
	private String hashPreviousOutput;

	public Approval(String hashPreviousOutput ,int amount, String ownerAddress, String holderSignature, String lockingScript) {

		this.amount = amount;
		this.holderSignature = holderSignature;
		this.ownerAddress = ownerAddress;
		this.lockingScript = lockingScript;
		this.hashPreviousOutput = hashPreviousOutput;
	}
	
	public void setHashPreviousOutput(String hashPreviousOutput){
		this.hashPreviousOutput = hashPreviousOutput;
	}
	
	public String getHashPreviousOutput(){
		return this.hashPreviousOutput;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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

	public byte[] getConcatedApproval() {

		// TODO [Vitali] May to concatenate more??? Did not Bitcoin say that
		// only the locking script is concatenated???
		// TODO [joeren]: Decrease or remove radix of string to byte array
		// conversion when the locking script is a real locking script
		byte[] lockingScript = ByteArray.convertFromString(getLockingScript(), Character.MAX_RADIX);
		return ByteArray.concatByteArrays(lockingScript);

	}

	@Override
	public String toString() {
		return "Approval [amount=" + amount + ", holderSignature=" + holderSignature + ", ownerAddress=" + ownerAddress
				+ ", lockingScript=" + lockingScript + ", hashPreviousOutput=" + hashPreviousOutput + "]";
	}
}
