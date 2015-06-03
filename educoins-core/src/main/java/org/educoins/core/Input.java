package org.educoins.core;

import org.educoins.core.utils.ByteArray;

public class Input {

	private static final int HEX = 16;
	
	private int index;
	private int amount;
	private String hashPrevOutput;
	private String[] unlockingScript;
	
	public Input(int amount, String hashPrevOutput, int index, String[] unlockingScript){
		
		this.amount = amount;
		this.hashPrevOutput = hashPrevOutput;
		this.index = index;
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

	public int getN() {
		return index;
	}
	
	public void setN(int n) {
		this.index = n;
	}
	
	public byte[] getUnlockingScript(EInputUnlockingScript signatureOrPublicKey) {	
		return ByteArray.convertFromString(this.unlockingScript[signatureOrPublicKey.getNumVal()], HEX);
	}

	public void setUnlockingScript(EInputUnlockingScript signatureOrPublicKey, String value) {		
		this.unlockingScript[signatureOrPublicKey.getNumVal()] = value;	
	}
	
	public byte[] getConcatedInput(){
		
		//TODO [Vitali] May to concatenate more??? Did not Bitcoin say that only the locking script is concatenated???
		return this.getUnlockingScript(EInputUnlockingScript.PUBLIC_KEY);
		
	}

	
	public enum EInputUnlockingScript{
		
		SIGNATURE(0),
		PUBLIC_KEY(1);
		
		private int enumVal;

		EInputUnlockingScript(int enumVal) {
	        this.enumVal = enumVal;
	    }

	    public int getNumVal() {
	        return enumVal;
	    }
		
		
	} 
	
	
	
	

}
