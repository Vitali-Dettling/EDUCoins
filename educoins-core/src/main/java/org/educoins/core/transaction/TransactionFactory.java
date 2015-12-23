package org.educoins.core.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class TransactionFactory implements ITransactionFactory {

	/* (non-Javadoc)
	 * @see org.educoins.core.transaction.ITransactionFactory#generateCoinbasedTransaction(int, java.lang.String)
	 */
	@Override
	public Transaction generateCoinbasedTransaction(int amount, String publicKey){
		Transaction coinTx = new CoinbaseTransaction(amount, publicKey);
		return coinTx.create();
	}
	
	/* (non-Javadoc)
	 * @see org.educoins.core.transaction.ITransactionFactory#generateRegularTransaction(java.util.List, int, java.lang.String)
	 */
	@Override
	public Transaction generateRegularTransaction(@NotNull List<Output> previousOutputs, int sendAmount, String sendPublicKey) {
		
		List<Output> copyPreviousOutputs = getEnoughPreviousOutputs(previousOutputs, sendAmount);	
		int outputAmoun = getSendedAmount(copyPreviousOutputs);	
		
		Transaction regTx = new RegularTransaction(copyPreviousOutputs, sendAmount, outputAmoun, sendPublicKey);
		return regTx.create();
	}

	/* (non-Javadoc)
	 * @see org.educoins.core.transaction.ITransactionFactory#generateRevokeTransaction(int, java.lang.String)
	 */
	@Override
	public Transaction generateRevokeTransaction(int amount, String lockingScript) {
		//TODO 
		return null;
	}

	/* (non-Javadoc)
	 * @see org.educoins.core.transaction.ITransactionFactory#generateApprovedTransaction(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Transaction generateApprovedTransaction(int amount, String owner, String holder, String lockingScript) {
		//TODO 
		return null;
	}
	
	private int getSendedAmount(List<Output> outputs){
		int amount = 0;
		for(Output out : outputs){
			amount += out.getAmount();
		}
		return amount;
	}
	
	private List<Output> getEnoughPreviousOutputs(List<Output> previousOutputs, int sendAmount){
		List<Output> copyPreviousOutputs = new ArrayList<>();
		
		Iterator<Output> iterator = previousOutputs.iterator();
		
		int enough = 0;
		while(iterator.hasNext()){
			Output out = iterator.next();
			enough += out.getAmount();
			copyPreviousOutputs.add(out);
			iterator.remove();
			if(enough >= sendAmount){
				break;
			}
		}
		return copyPreviousOutputs;
	}
	

}
