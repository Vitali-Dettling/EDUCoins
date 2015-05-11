package Transactions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.educoins.core.miner.Input;


public class Transaction{// implements List<T>TODO Macht das Sinn??? {

	
	
	/**
	 * Example of a real transaction, which is stored in a block.
	 * http://blockexplorer.com/tx/4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b#inputs
	 * 
	"in":[
    {
      "prev_out":{
        "hash":"0000000000000000000000000000000000000000000000000000000000000000",
        "n":4294967295
      },
      "coinbase":"04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73"
    }
    ],
    "out":[
      {
        "value":"50.00000000",
        "scriptPubKey":"04678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5f OP_CHECKSIG"
      }
    ]
	 */
	
	private static long version; 
	private static int inputCount; 
	private static int outputCount; 
	private static List<Input> inputs; 
	private static List<Output> outputs; 
	
	/**
	 * Book (Mastering Bitcoin): P.111
	 * 
	 * @param version: 4 bytes Specifies which rules this transaction follows
	 * @param inputCount: 1–9 bytes Input Counter How many inputs are included
	 * @param inputs: Variable Inputs One or more transaction inputs
	 * @param outputCounter: 1–9 bytes Output Counter How many outputs are included
	 * @param putputs: Variable Outputs One or more transaction outputs
	 * @param lockTime: 4 bytes A Unix timestamp or block number //TODO do we need this locktime???
	 * */
	public Transaction(long version, List<Input> inputs, List<Output> outputs){
		
		Transaction.version = version;
		Transaction.inputCount = inputs.size();
		Transaction.outputCount = outputs.size();
		Transaction.inputs = inputs;
		Transaction.outputs = outputs;
	}
	
	public static long getVersion() {
		return version;
	}

	public static int getInputCount() {
		return inputCount;
	}

	public static int getOutputCount() {
		return outputCount;
	}

	public static List<Input> getInputs() {
		return inputs;
	}

	public static List<Output> getOutputs() {
		return outputs;
	}
	
	
	
}
