package org.educoins.core;

import java.util.List;

public class ApprovedTransaction extends Transaction {
	
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
	public void setApprovals(List<Approval> approvals) {
		if (approvals == null) {
			// TODO [joeren]: change exception
			throw new IllegalArgumentException("argument vout must not be null");
		}

		if (approvals.size() == 0) {
			// TODO [joeren]: change exception
			throw new IllegalArgumentException("list approved must contain at least one element");
		}

		super.setApprovals(approvals);	
	}
}
