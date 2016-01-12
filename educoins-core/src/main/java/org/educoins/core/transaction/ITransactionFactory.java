package org.educoins.core.transaction;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface ITransactionFactory {

	Transaction generateCoinbasedTransaction(int amount, String publicKey);

	Transaction generateRegularTransaction(List<Output> previousOutputs, int sendAmount, String sendPublicKey);

	Transaction generateRevokeTransaction(int amount, String lockingScript);

	Transaction generateApprovedTransaction(List<Output> previousOutputs, int amount, String owner, String holderSignature, String lockingScript);

}