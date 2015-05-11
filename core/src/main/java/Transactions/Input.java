package Transactions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Input {
	
	
	/**
	 * http://blockexplorer.com/tx/4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b#inputs
	 */
	private static int amount;
	private static String prevousOutputHash;
	private static String fromAddress;
	private static EType type;
	private static String unlockingScript;
	
	
	public Input(int amount, String prevousOutputHash, String fromAddress, EType type, String unlockingScript){
		
		Input.amount = amount;
		Input.prevousOutputHash = prevousOutputHash;
		Input.fromAddress = fromAddress;
		Input.type = type;
		Input.unlockingScript = unlockingScript;
		
	}


	public static int getAmount() {
		return amount;
	}


	public static String getPrevousOutputHash() {
		return prevousOutputHash;
	}


	public static String getFromAddress() {
		return fromAddress;
	}


	public static EType getType() {
		return type;
	}


	public static String getScriptSig() {
		return unlockingScript;
	}


	



	
	
	

}
