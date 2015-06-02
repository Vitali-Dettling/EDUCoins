package org.educoins.core;

import org.educoins.core.utils.ByteArray;

public class Output {

	private int amount;
	private String dstPublicKey;
	private String lockingScript;
	
	public Output(int amount, String dstPublicKey, String lockingScript){
		
		this.amount = amount;
		this.dstPublicKey = dstPublicKey;
		this.lockingScript = lockingScript;
	}

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
	
	
	public byte[] getConcatedOutput(){
		
		//TODO [Vitali] May to concatenate more??? Did not Bitcoin say that only the locking script is concatenated???
		byte[] lockingScript = ByteArray.convertFromString(getLockingScript());
		return ByteArray.concatByteArrays(lockingScript);
		
	}

}
