package org.educoins.core.transaction;

import org.educoins.core.utils.Sha256Hash;

public class Revoke {

	private Sha256Hash hashPrevApproval;
	private int amount;
	private String ownerSig;
	private String ownerPubKey;

	public Revoke(Sha256Hash hashPrevApproval, int amount, String ownerPubKey) {
		this.hashPrevApproval = hashPrevApproval;
		this.amount = amount;
		this.ownerPubKey = ownerPubKey;
	}

	public Sha256Hash getHashPrevApproval() {
		return hashPrevApproval;
	}

	public void setHashPrevApproval(Sha256Hash hashPrevApproval) {
		this.hashPrevApproval = hashPrevApproval;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getOwnerSig() {
		return ownerSig;
	}

	public void setOwnerSig(String ownerSig) {
		this.ownerSig = ownerSig;
	}

	public String getOwnerPubKey() {
		return ownerPubKey;
	}

	public void setOwnerPubKey(String ownerPubKey) {
		this.ownerPubKey = ownerPubKey;
	}

	@Override
	public String toString() {
		return "Revoke [hashPrevApproval=" + hashPrevApproval + ", amount=" + amount + ", ownerSig=" + ownerSig
				+ ", ownerPubKey=" + ownerPubKey + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + ((hashPrevApproval == null) ? 0 : hashPrevApproval.hashCode());
		result = prime * result + ((ownerPubKey == null) ? 0 : ownerPubKey.hashCode());
		result = prime * result + ((ownerSig == null) ? 0 : ownerSig.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Revoke other = (Revoke) obj;
		if (amount != other.amount)
			return false;
		if (hashPrevApproval == null) {
			if (other.hashPrevApproval != null)
				return false;
		} else if (!hashPrevApproval.equals(other.hashPrevApproval))
			return false;
		if (ownerPubKey == null) {
			if (other.ownerPubKey != null)
				return false;
		} else if (!ownerPubKey.equals(other.ownerPubKey))
			return false;
		if (ownerSig == null) {
			if (other.ownerSig != null)
				return false;
		} else if (!ownerSig.equals(other.ownerSig))
			return false;
		return true;
	}
	
	
}
