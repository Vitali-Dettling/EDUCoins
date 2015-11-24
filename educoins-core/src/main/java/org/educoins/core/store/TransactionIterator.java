package org.educoins.core.store;

import java.util.Arrays;

import org.educoins.core.Block;
import org.educoins.core.Input;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.jetbrains.annotations.NotNull;

public class TransactionIterator implements ITransactionIterator {

    private IBlockIterator blockIterator;
	private Block block = null;
	
	public TransactionIterator(@NotNull IBlockStore blockStore, @NotNull byte[] genesisHash) {
		this.blockIterator = new BlockIterator(blockStore, genesisHash);
		this.block = blockStore.getLatest();
	}

	@Override
	public Output previous(@NotNull Input startInput) {
		
		byte[] outID = startInput.getHashPrevOutput().getBytes();
		Output previousOutput = null;
		
		if(this.blockIterator.hasNext()){
			this.block = this.blockIterator.next();
		}

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
		
		if(previousOutput == null){
			previous(startInput);
		}
		return previousOutput;
	}
}
