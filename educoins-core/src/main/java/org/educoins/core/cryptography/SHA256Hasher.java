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

    // TODO: [Michael] Instead of a synchronized method create a new Digest
    // for every call, we need a threadsafe solution, so we can parallelize
    // the block verification.
    public static synchronized byte[] hash(byte[] byteArray) {
        return SHA256Hasher.messageDigest.digest(byteArray);
    }

}
