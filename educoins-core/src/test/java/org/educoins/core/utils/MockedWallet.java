package org.educoins.core.utils;

import java.io.IOException;
import java.util.List;

import org.educoins.core.Wallet;

public class MockedWallet {
	
	public static String getPublicKey(){
		return Wallet.getPublicKey();
	}

	public static String getSignature(String publicKey, String hash){
		return Wallet.getSignature(publicKey, hash);
	}

	public static boolean checkSignature(String randomNumber, String signature) {
		return Wallet.checkSignature(randomNumber, signature);
	}

	public static List<String> getPublicKeys() {
		return Wallet.getPublicKeys();
	}

	public static boolean compare(String message, String signature, String publicKey) {
		return Wallet.compare(message, signature, publicKey);
	}
	
	public static void delete(){
		if (!MockedIO.deleteDefaultWalletStore()){
			throw new IllegalStateException("Db could not be deleted!");
		}
	}
}
