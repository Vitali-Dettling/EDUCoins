package org.educoins.core;

import java.util.List;

public class RegularTransaction extends Transaction {

	@Override
	public void setInputs(List<Input> vin) {
		if (vin == null) {
			// TODO [joeren]: change exception
			throw new IllegalArgumentException("argument vin must not be null");
		}

		if (vin.size() == 0) {
			// TODO [joeren]: change exception
			throw new IllegalArgumentException("list vin must contain at least one element");
		}

		super.setInputs(vin);
	}

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
