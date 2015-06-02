package org.educoins.core;

import org.educoins.core.utils.ByteArray;

public class Input {

	private static final int HEX = 16;
	private static final String SEPERATOR = " ";
	
	private int index;
	private int amount;
	private String hashPrevOutput;
	private String unlockingScript;
	
	public Input(int amount, String hashPrevOutput, int index, String unlockingScript){
		
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
	
	public byte[] getUnlockingScript(EInputUnlockingScriptSeperator seperator) {
		//TODO [Vitali] Mit Jören klären ob das OK is.
		String[] scriptContent = this.unlockingScript.split(SEPERATOR);
		return ByteArray.convertFromString(scriptContent[EInputUnlockingScriptSeperator.SIGNATURE.getNumVal()], HEX);
	}

	public void setUnlockingScript(String unlockingScript) {
		this.unlockingScript = unlockingScript;
	}
	
	public byte[] getConcatedInput(){
		
		//TODO [Vitali] May to concatenate more??? Did not Bitcoin say that only the locking script is concatenated???
		byte[] unlockingScript = ByteArray.convertFromString(this.unlockingScript);
		return ByteArray.concatByteArrays(unlockingScript);
		
	}

	
	public enum EInputUnlockingScriptSeperator{
		
		SIGNATURE(0),
		publicKEy(1);
		
		private int enumVal;

		EInputUnlockingScriptSeperator(int enumVal) {
	        this.enumVal = enumVal;
	    }

	    public int getNumVal() {
	        return enumVal;
	    }
		
		
	} 
	
	
	
	

}
