package org.educoins.core.cryptography;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

public class ECDSA {
	
	private static final String ECDSA = "EC";
	private static final String SHA256_WITH_ECDSA = "SHA256withECDSA";
	private static final int ADDRESS_SPACE_256 = 256;
	private static final int HEX = 16;
	private static final int BIG_ENDIAN_ALWAYS_POSITIVE = 1;
	
	private KeyPairGenerator keyPairGenerator;
	private KeyPair keyPair;
	private Signature signature;
	
	//TODO Herausfinden wie man diese Referenzieren kann, damit man weis welcher Public Key zu welcher Signature usw. gehört???
	
	public ECDSA(){
		
        try {
			this.keyPairGenerator = KeyPairGenerator.getInstance(ECDSA);
			this.keyPairGenerator.initialize(ADDRESS_SPACE_256, new SecureRandom());
		    this.keyPair = this.keyPairGenerator.generateKeyPair();
		    
			this.signature = Signature.getInstance(SHA256_WITH_ECDSA);
			this.signature.initSign(this.keyPair.getPrivate());
		    
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	
	public String getPublicKey() {
		return new BigInteger(BIG_ENDIAN_ALWAYS_POSITIVE, this.keyPair.getPublic().getEncoded()).toString(HEX);
	}

    public String getPrivateKey() {
    	return new BigInteger(BIG_ENDIAN_ALWAYS_POSITIVE, this.keyPair.getPrivate().getEncoded()).toString(HEX);
    }
    	
    /**
     * Create a Signature object and initialize it with the private key.
     * If null it only return another signature.
     * */
    public String getSignature(String message) {

    	try {
    		//TODO Ausprobieren ob die if(message != null){ wirklich nötig ist, oder ist es egal, wenn man mehrmal die message übergibt???
    		if(message != null){
	    		this.signature = Signature.getInstance(SHA256_WITH_ECDSA);
				this.signature.initSign(this.keyPair.getPrivate());
	    		this.signature.update(message.getBytes("UTF-8"));
    		}
			return  new BigInteger(BIG_ENDIAN_ALWAYS_POSITIVE, signature.sign()).toString(HEX);
			
		} catch (SignatureException | NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    	
    }
    
    
    
    
    
}