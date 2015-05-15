package org.educoins.core.cryptography;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

/**
 * @information What is a private/public key and signature.
 * 
 * Book [Mastering Bitcoin]: P63
 * 
 * The private key is just a number. You can pick your private
 * keys randomly using just a coin, pencil, and paper: toss a coin 256
 * times and you have the binary digits of a random private key you can
 * use in a wallet. The public key can then be generated from the
 * private key.
 * */
public class ECDSA {
	
	private static final String ECDSA = "EC";
	private static final String SHA256_WITH_ECDSA = "SHA256withECDSA";
	private static final int ADDRESS_SPACE_256 = 256;
	
	private KeyPairGenerator keyPairGenerator;
	private KeyPair keyPair;
	private Signature signature;

	
	/**
	 * Public/private key verification for the Elliptic Curve Digital Signature Algorithm (ECDSA).
	 * 
	 * @throws NoSuchAlgorithmException If e.g. the ECDSA does not exist. 
	 * 
	 * */
	public ECDSA() throws NoSuchAlgorithmException{
		
		this.keyPairGenerator = KeyPairGenerator.getInstance(ECDSA);
		this.keyPairGenerator.initialize(ADDRESS_SPACE_256, new SecureRandom());
	    this.keyPair = this.keyPairGenerator.generateKeyPair();  
		this.signature = Signature.getInstance(SHA256_WITH_ECDSA);
	}
	
	/**
	 * Returns the public key as string value, because it is really just a random number.
	 * 
	 * */
	public String getPublicKey() {
		return this.keyPair.getPublic().getEncoded().toString();
	}

	/**
	 * Returns the private key as string value, because it is really just a random number.
	 * 
	 * */
    public String getPrivateKey() {
    	return this.keyPair.getPrivate().getEncoded().toString();
    }
    
    /**
     * Initialize the signature it with the a key.
     * 
     * @warning For verification purpose one need to initial the signature each time.
     * 
     * @throws Exception [Class ECDSA] The signature transaction hash value cannot be null.
     * 
     * */
    public void initSignature(String hashedTranscation) throws Exception {
    	
    	if(hashedTranscation == null){
    		throw new Exception("EXCEPTION: [Class ECDSA] The signature transaction hash value cannot be null.");
    	}

    	this.signature.initSign(this.keyPair.getPrivate());
    	this.signature.update(hashedTranscation.getBytes());
    }
    
    /**
     * Verify the signature with a hashed transaction.
     * 
     * @warning For verification purpose one need to initial the signature each time.
     * 
     * @throws Exception  [Class ECDSA] The signature transaction hash value cannot be null.
     * 
     * */
    public boolean verifySignature(String hashedTranscation, byte[] signature) throws Exception {

		if(hashedTranscation == null){
			throw new Exception("EXCEPTION: [Class ECDSA] The signature transaction hash value cannot be null.");
		}

		this.signature.initVerify(this.keyPair.getPublic());
		this.signature.update(hashedTranscation.getBytes());
		
		return this.signature.verify(signature);
    }

    /**
     * Create a Signature object.
     * 
     * @warning For verification purpose one need to initial the signature each time.
     * 
     * @throws SignatureException Please note the warning.
     * */
    public byte[] getSignature() throws SignatureException {
		return  this.signature.sign();
    }
    
    
    
    
    
    
    
    
    
}