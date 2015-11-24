package org.educoins.core;

import org.educoins.core.utils.Sha256Hash;

public class Output {
	
	private int amount;
	private Sha256Hash dstPublicKey;
	private Sha256Hash lockingScript;
	
	public Output(int amount, Sha256Hash dstPublicKey, Sha256Hash lockingScript){
		
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

	public Sha256Hash getDstPublicKey() {
		return this.dstPublicKey;
	}

	public void setDstPublicKey(Sha256Hash dstPublicKey) {
		this.dstPublicKey = dstPublicKey;
	}

	public Sha256Hash getLockingScript() {
		return this.lockingScript;
	}

	public void setLockingScript(Sha256Hash lockingScript) {
		this.lockingScript = lockingScript;
	}
		
	public byte[] getConcatedOutput(){
		
		//TODO [Vitali] May to concatenate more??? Did not Bitcoin say that only the locking script is concatenated???
		byte[] lockingScript = getLockingScript().getBytes();
		return lockingScript;
		
	}

}
