package org.educoins.core.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.educoins.core.Wallet;
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
	 * @see org.educoins.core.transaction.ITransactionFactory#generateRevokeTransaction(int, java.lang.String)
	 */
	@Override
	public Transaction generateRevokeTransaction(List<Transaction> approvedTransactions, String transToRevokeHash) {
		Transaction revTx = new RevokeTransaction(approvedTransactions, transToRevokeHash);
		return revTx.create();
	}
	
	/* (non-Javadoc)
	 * @see org.educoins.core.transaction.ITransactionFactory#generateApprovedTransaction(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Transaction generateApprovedTransaction(@NotNull List<Output> previousOutputs, int toApproveAmount, String owner, String holderSignature, String lockingScript) {
		
		int outputAmouns = getOutputAmount(previousOutputs);
		List<Approval> approvals = createApprovals(toApproveAmount, owner, lockingScript);
		List<Output> outputs = createOutputs(toApproveAmount, outputAmouns, owner, true);
		List<Input> inputs = createInputs(previousOutputs);
		
		Transaction regTx = new ApprovedTransaction(approvals, outputs, inputs, holderSignature);
		return regTx.create();
	}

	/* (non-Javadoc)
	 * @see org.educoins.core.transaction.ITransactionFactory#generateRegularTransaction(java.util.List, int, java.lang.String)
	 */
	@Override
	public Transaction generateRegularTransaction(@NotNull List<Output> previousOutputs, int sendAmount, String sendPublicKey) {
	
		int outputAmounts = getOutputAmount(previousOutputs);
		List<Output> outputs = createOutputs(sendAmount, outputAmounts, sendPublicKey, false);
		List<Input> inputs = createInputs(previousOutputs);
		
		Transaction regTx = new RegularTransaction(outputs, inputs);
		return regTx.create();
	}
	
	
	private List<Approval> createApprovals(int toApproveAmount, String ownerAddress, String lockingScript){
		List<Approval> approvals = new ArrayList<>();

		Approval approval = new Approval(toApproveAmount, ownerAddress, lockingScript);
		approvals.add(approval);
		
		return approvals;
		
	}
	
	//Shows within a transaction (input), where the EDUCoins come from. 
	public List<Input> createInputs(List<Output> previousOutputs) {

		List<Input> inputs = new ArrayList<>();
		Iterator<Output> iterator = previousOutputs.iterator();
		//Iterates through all outputs, adds it into the input and removes them.
		while(iterator.hasNext()){
			Output out = iterator.next();
			Input in = new Input(out.getAmount(), out.hash(), out.getLockingScript());
			inputs.add(in);
			iterator.remove();
		}
		return inputs;
	}
	
	public List<Output> createOutputs(int sendAmount, int inputAmount, String sendPublicKey, boolean isApprovalTx) {
		
		List<Output> outputs = new ArrayList<>();
		// Send to new owner.
		if(!isApprovalTx){
			Output out1 = new Output(sendAmount, sendPublicKey);
			outputs.add(out1);
		}
		
		String reversePublicKey = Wallet.getPublicKey();

		// Remaining amount send back to the original owner.
		int reverseAmount = inputAmount - sendAmount;
		if (reverseAmount > 0) {
			// Only a reverse amount if there is one. Otherwise the transaction
			// would not verify.
			Output out2 = new Output(reverseAmount, reversePublicKey);
			outputs.add(out2);
		}
		return outputs;
	}
	
	private int getOutputAmount(List<Output> outputs){
		int amount = 0;
		for(Output out : outputs){
			amount += out.getAmount();
		}
		return amount;
	}
	
}
