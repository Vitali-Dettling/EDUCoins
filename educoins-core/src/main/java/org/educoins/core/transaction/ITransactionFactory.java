package org.educoins.core.transaction;

import java.util.List;

import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;

public interface ITransactionFactory {

	Transaction generateCoinbasedTransaction(int amount, String publicKey);

	Transaction generateRegularTransaction(List<Output> previousOutputs, int sendAmount, String sendPublicKey);

	Transaction generateRevokeTransaction(List<Transaction> approvedTransactions, Sha256Hash transToRevokeHash);

	Transaction generateApprovedTransaction(List<Output> previousOutputs, int amount, String owner, String holderSignature, String lockingScript);


}