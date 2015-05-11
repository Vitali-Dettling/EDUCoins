package org.educoins.core.miner;

public enum EScripts {
	
	//TODO Wir werden wohl nicht alle brauchen, oder durch andere Befehel ersetzen...
	/**
	 * DUP: It doubles the current parameter lying on top.
	 * HASH160: It is a sha160 hash function which generates a Bitcoin address of the public key.
	 * EQUALVERIFY: It verifies if two Bitcoin addresses are correct.
	 * CHECKSIG: It checks the signature and the public key of the Locking Script and verifies whether they match each other.
	 * <sig>: Signature derived from the private key and the transaction (message).
	 * <PubK>: Public key derived from the private key.
	 * <PubKHash>: Hashed public key with sha160 hash function to a Bitcoin address. As this Bitcoin address has been published, the participants have sent Bitcoins to it.
	 * 
	 * */
	
	DUB,
	HASH160,
	EQUALVERIFY,
	CHECKSIG,
	

}
