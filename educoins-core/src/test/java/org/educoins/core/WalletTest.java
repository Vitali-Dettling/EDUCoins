package org.educoins.core;

import org.educoins.core.utils.Generator;
import org.educoins.core.utils.ByteArray;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WalletTest {

	private static final int KEYS = 10;
	
	@Test
	public void testNumberOfPublicKeys() throws IOException{
		
		List<String> toVerifyKeys = new ArrayList<String>();
		for(int i = 0 ; i < KEYS ; i++ ){
			Wallet.getPublicKey();
		}
		toVerifyKeys.addAll(Wallet.getPublicKeys());
		
		List<String> storedKeys = Wallet.getPublicKeys();
		//Number of generated and stored keys should be equal.
		assertEquals(storedKeys.size(), toVerifyKeys.size());
		
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
		
		assertEquals(countHits, storedKeys.size());
	}
	
	@Test
	public void testSignatureVerification() {

		String randomNumber = Generator.getSecureRandomString256HEX();
		
		String pubKey = Wallet.getPublicKey();
		String signature = Wallet.getSignature(pubKey, randomNumber);
		
		assertNotNull(pubKey);
		assertNotNull(signature);
		
		boolean verified = Wallet.checkSignature(randomNumber, signature);
		assertTrue(verified);
	}
	
}
