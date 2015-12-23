package org.educoins.core.transaction;

import org.educoins.core.cryptography.SHA256Hasher;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Hashable;
import org.educoins.core.utils.Sha256Hash;

public class Output implements Hashable  {
	
	private int amount;
	private String lockingScript;
	
	public Output(int amount, String lockingScript){
		
		this.amount = amount;
		this.lockingScript = lockingScript;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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

	@Override
	public String toString() {
		return "Output [amount=" + amount + ", lockingScript=" + lockingScript + "]";
	}

	@Override
	public Sha256Hash hash() {
		byte[] toBeHashed = getConcatedOutput();
		return Sha256Hash.wrap(SHA256Hasher.hash(toBeHashed));
	}
	

}
