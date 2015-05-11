package Transactions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Output{
	
	private static int amount;
	private static String toAddress;
	private static EType type;
	private static String lockingScript;
	
	
	public Output(int amount, String toAddress, EType type, String lockingScript){
		
		Output.amount = amount;
		Output.toAddress = toAddress;
		Output.type = type;
		Output.lockingScript = lockingScript;
	}

	public static int getAmount() {
		return amount;
	}


	public static String getToAddress() {
		return toAddress;
	}


	public static EType getType() {
		return type;
	}


	public static String getLockingScript() {
		return lockingScript;
	}

	
	
	
}
