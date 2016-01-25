package org.educoins.core.transaction;

import org.educoins.core.cryptography.SHA256Hasher;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Hashable;
import org.educoins.core.utils.Sha256Hash;

public class Revoke {
	
	private Sha256Hash hashPrevApproval;
	private int amount;
	private String ownerSig;
	private String ownerPubKey;
	
	
	public Revoke(Sha256Hash hashPrevApproval, int amount, String ownerPubKey){
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
}
