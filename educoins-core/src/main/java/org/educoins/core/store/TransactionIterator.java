package org.educoins.core.store;

import java.util.Arrays;

import org.educoins.core.Block;
import org.educoins.core.Input;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.utils.ByteArray;
import org.jetbrains.annotations.NotNull;

public class TransactionIterator implements ITransactionIterator {

    private IBlockIterator blockIterator;
	private static Block block = null;
	
	public TransactionIterator(@NotNull IBlockStore blockStore, @NotNull byte[] genesisHash) {
		this.blockIterator = new BlockIterator(blockStore, genesisHash);
		block = blockStore.getLatest();
	}

	@Override
	public Output previous(@NotNull Input startInput) {
		
		if(block == null){
			//Should only occur if there is no blockchain. 
			return null;
		}
		
		byte[] outID = ByteArray.convertFromString(startInput.getHashPrevOutput());
		Output previousOutput = null;
		
		for(Transaction tx : block.getTransactions()){
			for(Output out : tx.getOutputs()){
				byte[] outByte = out.getConcatedOutput();
				if(outByte.length == outID.length){
		        	if(Arrays.equals(outByte, outID)){
		        		previousOutput = out;
		        		break;
		        	};
			        
				}
			}
		}
		
		if(this.blockIterator.hasNext()){
			block = this.blockIterator.next();
			previous(startInput);
		}
		return previousOutput;
	}
}
