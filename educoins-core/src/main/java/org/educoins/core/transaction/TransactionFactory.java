package org.educoins.core.transaction;

import java.util.List;

import org.educoins.core.utils.Sha256Hash;
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
	 * @see org.educoins.core.transaction.ITransactionFactory#generateApprovedTransaction(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Transaction generateApprovedTransaction(@NotNull List<Output> previousOutputs, int amount, String owner, String holderSignature, String lockingScript) {
		
		Transaction regTx = new ApprovedTransaction(previousOutputs, amount, owner, holderSignature, lockingScript);
		return regTx.create();
	}

	/* (non-Javadoc)
	 * @see org.educoins.core.transaction.ITransactionFactory#generateRevokeTransaction(int, java.lang.String)
	 */
	@Override
	public Transaction generateRevokeTransaction(Sha256Hash transToRevokeHash, String lockingScript) {
		Transaction revTx = new RevokeTransaction(transToRevokeHash, lockingScript);
		return revTx.create();
	}
	
	/* (non-Javadoc)
	 * @see org.educoins.core.transaction.ITransactionFactory#generateRegularTransaction(java.util.List, int, java.lang.String)
	 */
	@Override
	public Transaction generateRegularTransaction(@NotNull List<Output> previousOutputs, int sendAmount, String sendPublicKey) {
	
		int outputAmoun = getSentAmount(previousOutputs);
		
		Transaction regTx = new RegularTransaction(previousOutputs, sendAmount, outputAmoun, sendPublicKey);
		return regTx.create();
	}
	
	private int getSentAmount(List<Output> outputs){
		int amount = 0;
		for(Output out : outputs){
			amount += out.getAmount();
		}
		return amount;
	}
	
	//TODO Delete
//	private List<Output> getEnoughPreviousOutputs(List<Output> previousOutputs, int sendAmount){
//		List<Output> copyPreviousOutputs = new ArrayList<>();
//		
//		Iterator<Output> iterator = previousOutputs.iterator();
//		
//		int enough = 0;
//		while(iterator.hasNext()){
//			Output out = iterator.next();
//			enough += out.getAmount();
//			copyPreviousOutputs.add(out);
//			iterator.remove();
//			if(enough >= sendAmount){
//				break;
//			}
//		}
//		return copyPreviousOutputs;
//	}
	

}