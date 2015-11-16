package org.educoins.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Wallet;
import org.educoins.core.test.utils.Generator;
import org.educoins.core.utils.ByteArray;
import org.junit.Before;
import org.junit.Test;

public class WalletTest {

	private static final int HEX = 16;
	private static final int KEYS = 10;
	
	private Generator randomNumber;
	private Wallet wallet;
	
	@Before
	public void setUp(){
		this.randomNumber = new Generator();
		this.wallet = new Wallet();
	}
	
	@Test
	public void testSignatureVerification() {

		String randomNumber = this.randomNumber.getSecureRandomString256HEX();
		
		String pubKey = wallet.getPublicKey();
		String signature = wallet.getSignature(pubKey, randomNumber);
		
		assertNotNull(pubKey);
		assertNotNull(signature);
		
		byte[] sig = ByteArray.convertFromString(signature, HEX);
		boolean verified = wallet.checkSignature(randomNumber, sig);
		assertTrue(verified);
	}

	@Test
	public void testNumberOfPublicKeys() throws IOException{
		
		String key;
		List<String> toVerifyKeys = new ArrayList<String>();
		for(int i = 0 ; i < KEYS ; i++ ){
			key = this.wallet.getPublicKey();
			toVerifyKeys.add(key);
		}
		
		List<String> storedKeys = this.wallet.getPublicKeys();
		//Number of generated and stored keys should be equal.
		assertEquals(storedKeys, toVerifyKeys);
		
		//Checks of all checks correspond to each other. 
		int countHits = 0;
		for(String sKey : storedKeys){
			for(String vKey : toVerifyKeys){	
				if(vKey.equals(sKey)){
					countHits++;
					break;
				}
			}
		}
		
		assertEquals(countHits, KEYS);
	}
	
	@Test
	public void testVerifySignaturek(){
		final String publicKey = this.wallet.getPublicKey();
		final String message = "ABCDEF";
		final String signature = this.wallet.getSignature(publicKey, message);
		
		boolean testResult = this.wallet.compare(message, signature, publicKey);
		assertTrue(testResult);
	}
	
	
	@Test
	public void testSignatureCheck(){
		final String publicKey = this.wallet.getPublicKey();
		final String message = "ABC";
		final String signature = this.wallet.getSignature(publicKey, message);
		
		byte[] sign = ByteArray.convertFromString(signature, HEX);
		
		boolean testResult = this.wallet.checkSignature(message, sign);
		assertTrue(testResult);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
