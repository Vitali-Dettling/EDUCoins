package org.educoins.core;

import java.util.List;

public class CoinbaseTransaction extends Transaction {

	@Override
	public void setOutputs(List<Output> vout) {
		if (vout == null) {
			// TODO [joeren]: change exception
			throw new IllegalArgumentException("argument vout must not be null");
		}

		if (vout.size() == 0) {
			// TODO [joeren]: change exception
			throw new IllegalArgumentException("list vout must contain at least one element");
		}

		super.setOutputs(vout);
	}

}
