package org.educoins.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Generator;
import org.educoins.core.utils.IO;
import org.educoins.core.utils.IO.EPath;
import org.junit.Before;
import org.junit.Test;

import educoins.core.utils.Generator;
import educoins.core.utils.MockedWallet;

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
	
	private Generator randomNumber;
	private Wallet wallet;
	
	@Before
	public void setUp(){
		this.randomNumber = new Generator();
		this.wallet = new Wallet(IO.getDefaultFileLocation(EPath.TMP, EPath.WALLET));
	}
	

	@Test
	public void testSignatureVerification() {

		String randomNumber = Generator.getSecureRandomString256HEX();
		
		String pubKey = MockedWallet.getPublicKey();
		String signature = MockedWallet.getSignature(pubKey, randomNumber);
		
		assertNotNull(pubKey);
		assertNotNull(signature);
		
		byte[] sig = ByteArray.convertFromString(signature, HEX);
		boolean verified = MockedWallet.checkSignature(randomNumber, sig);
		assertTrue(verified);
	}

	@Test
	public void testNumberOfPublicKeys() {
		
		String key;
		List<String> toVerifyKeys = new ArrayList<String>();
		for(int i = 0 ; i < KEYS ; i++ ){
			key = MockedWallet.getPublicKey();
			toVerifyKeys.add(key);
		}
		
		List<String> storedKeys = MockedWallet.getPublicKeys();		
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
		final String publicKey = MockedWallet.getPublicKey();
		final String message = Generator.getSecureRandomString256HEX();
		final String signature = MockedWallet.getSignature(publicKey, message);
		
		boolean testResult = MockedWallet.compare(message, signature, publicKey);
		assertTrue(testResult);
	}
	
	
	@Test
	public void testSignatureCheck(){
		final String publicKey = MockedWallet.getPublicKey();
		final String message = Generator.getSecureRandomString256HEX();
		final String signature = MockedWallet.getSignature(publicKey, message);
		
		byte[] sign = ByteArray.convertFromString(signature, HEX);
		
		boolean testResult = MockedWallet.checkSignature(message, sign);
		assertTrue(testResult);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
