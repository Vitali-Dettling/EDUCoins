package org.educoins.core.miner;

//Someone interested in transactions events
public class Input implements IListen {

	@Override
	public void sendTransaction(String receivedTransacti) {
		System.out.println(receivedTransacti);
	}

}
