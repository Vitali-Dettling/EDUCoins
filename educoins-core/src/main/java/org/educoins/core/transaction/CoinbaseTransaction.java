package org.educoins.core.transaction;

import java.util.ArrayList;

public class CoinbaseTransaction extends Transaction {

    public int amount;
    public String publicKey;

    public CoinbaseTransaction(int amount, String publicKey) {
        this.amount = amount;
        this.publicKey = publicKey;
    }

    @Override
    public Transaction create() {
        Output out = new Output(this.amount, this.publicKey);
        super.setOutputs(new ArrayList<Output>() {{
            add(out);
        }});
        return this;
    }

}
