package org.educoins.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.Generator;
import org.educoins.core.utils.MockedWallet;
import org.junit.Test;

public class WalletTest {

	private static final int KEYS = 10;

	@Test
	public void testWrongSignature() {

		Transaction tx = BlockStoreFactory.generateTransaction(1);

		String hashedTranscation = tx.hash().toString();
		String publicKey = MockedWallet.getPublicKey();

		String signatureTest = Wallet.getSignature(publicKey, hashedTranscation);
		char symbols[] = { ' ', '.', ';', '-', 'a', 'd', 'w', '1', '*', '+', '~', '|' };

		for (char sym : symbols) {
			signatureTest += signatureTest + sym;
			assertFalse(Wallet.compare(hashedTranscation, signatureTest, publicKey));
		}
	}

	@Test
	public void testSameSignature() {

		Transaction tx = BlockStoreFactory.generateTransaction(1);

		String hashedTranscation = tx.hash().toString();
		String publicKey = MockedWallet.getPublicKey();

		String signatureTest = Wallet.getSignature(publicKey, hashedTranscation);
		String signatureResult = Wallet.getSignature(publicKey, hashedTranscation);

		assertTrue(Wallet.compare(hashedTranscation, signatureTest, publicKey));
		assertTrue(Wallet.compare(hashedTranscation, signatureResult, publicKey));
	}

	@Test
	public void testNumberOfPublicKeys() throws IOException {

		List<String> toVerifyKeys = new ArrayList<String>();
		for (int i = 0; i < KEYS; i++) {
			Wallet.getPublicKey();
		}
		toVerifyKeys.addAll(Wallet.getPublicKeys());

		List<String> storedKeys = Wallet.getPublicKeys();
		// Number of generated and stored keys should be equal.
		assertEquals(storedKeys.size(), toVerifyKeys.size());

		// Checks of all checks correspond to each other.
		int countHits = 0;
		for (String sKey : storedKeys) {
			for (String vKey : toVerifyKeys) {
				if (vKey.equals(sKey)) {
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
