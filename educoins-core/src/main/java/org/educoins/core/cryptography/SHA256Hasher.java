package org.educoins.core.cryptography;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hasher {

	private static MessageDigest messageDigest;

	static {
		try {
			SHA256Hasher.messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static byte[] hash(byte[] byteArray) {
		return SHA256Hasher.messageDigest.digest(byteArray);
	}

}
