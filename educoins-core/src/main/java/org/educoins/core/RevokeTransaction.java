package org.educoins.core;

import org.educoins.core.cryptography.SHA256Hasher;
import org.educoins.core.utils.Sha256Hash;

public class RevokeTransaction extends Transaction {

    private Sha256Hash approvedTransaction = null;

    public RevokeTransaction(Transaction trans) {
        super();
        this.approvedTransaction = Sha256Hash.wrap(trans.hash());
    }

    @Override
    public ETransaction whichTransaction() {
        return ETransaction.REVOKE;
    }

    @Override
    public byte[] hash() {
        return SHA256Hasher.hash(SHA256Hasher.hash(approvedTransaction.getBytes()));
    }
}
