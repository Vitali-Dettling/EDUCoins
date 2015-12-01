package org.educoins.core.utils;

import java.io.IOException;
import java.util.List;

import org.educoins.core.Wallet;

public class MockedWallet {
	
	private static Wallet wallet = new Wallet(MockedIO.getDefaultWalletStore());	;
	
	public static String getPublicKey(){
		return wallet.getPublicKey();
	}

	public static String getSignature(String publicKey, String hash){
		return wallet.getSignature(publicKey, hash);
	}

	public static boolean checkSignature(String randomNumber, byte[] signature) {
		return wallet.checkSignature(randomNumber, signature);
	}

	public static List<String> getPublicKeys() {
		try {
			return wallet.getPublicKeys();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean compare(String message, String signature, String publicKey) {
		return wallet.compare(message, signature, publicKey);
	}

	public static Wallet getMockedWallet() {
		return wallet;
	}
	
	public static void delete(){
		if (!MockedIO.deleteDefaultWalletStore()){
			throw new IllegalStateException("Db could not be deleted!");
		}
	}
}
