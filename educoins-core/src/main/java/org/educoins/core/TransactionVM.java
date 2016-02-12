package org.educoins.core;

import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.Sha256Hash;

/**
 * Created by Marvin on 15.01.2016.
 */
public class TransactionVM {

    private int amount;
    private Sha256Hash sender;
    private Sha256Hash receiver;
    
    public Transaction.ETransaction getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Transaction.ETransaction transactionType) {
        this.transactionType = transactionType;
    }

    private Transaction.ETransaction transactionType;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Sha256Hash getSender() {
        return sender;
    }

    public void setSender(Sha256Hash sender) {
        this.sender = sender;
    }

    public Sha256Hash getReceiver() {
        return receiver;
    }

    public void setReceiver(Sha256Hash receiver) {
        this.receiver = receiver;
    }

    public void setHash(Sha256Hash hash) {
        this.hash = hash;
    }

    private Sha256Hash hash;

    public Sha256Hash getHash() {
        return hash;
    }
}

