package org.educoins.core;

import org.educoins.core.cryptography.SHA256Hasher;
import org.educoins.core.utils.CannotRevokeRevokeTransactionException;
import org.educoins.core.utils.Sha256Hash;

public class RevokeTransaction extends Transaction {

    private Sha256Hash approvedTransaction = null;

    public RevokeTransaction(Transaction transaction) throws CannotRevokeRevokeTransactionException {
        super();
        if (transaction.whichTransaction() == ETransaction.REVOKE) {
            throw new CannotRevokeRevokeTransactionException();
        }

        this.approvedTransaction = transaction.hash();
        for (int i = 0; i < transaction.getApprovals().size(); i++) {
            Input input = new Input(transaction.getApprovals().get(i).getAmount(), transaction.hash().toString(), i);
            this.addInput(input);
        }
        this.setOutputs(transaction.getOutputs());
        transaction.setOutputs(null);
    }

    public Sha256Hash getApprovedTransaction() {
        return approvedTransaction;
    }

    @Override
    public ETransaction whichTransaction() {
        return ETransaction.REVOKE;
    }

    @Override
    public Sha256Hash hash() {
        return Sha256Hash.wrap(SHA256Hasher.hash(SHA256Hasher.hash(approvedTransaction.getBytes())));
    }
}
