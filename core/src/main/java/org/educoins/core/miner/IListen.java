package org.educoins.core.miner;

// An interface to be implemented by everyone interested in "transaction" events
public interface IListen {

	public void sendTransaction(String receivedTransacti);

}
