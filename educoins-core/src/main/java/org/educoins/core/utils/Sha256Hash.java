package org.educoins.core.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

import org.educoins.core.cryptography.SHA256Hasher;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;

/**
 * A Sha256Hash just wraps a byte[] so that equals and hashcode work correctly, allowing it to be used as keys in a
 * map. It also checks that the length is correct and provides a bit more type safety.
 */
public class Sha256Hash implements Serializable, Comparable<Sha256Hash> {

    static BaseEncoding Hex = BaseEncoding.base16().lowerCase();
    /**
	 * 
	 */
	private static final long serialVersionUID = -9086690286714208838L;
	private final byte[] bytes;
    public static final Sha256Hash ZERO_HASH = wrap(new byte[32]);
    public static final Sha256Hash MAX_HASH = wrap(ByteArray.convertFromString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));

    

    public Sha256Hash(byte[] bytes) {
		this.bytes = bytes;
	}


    /**
     * Calculates the (one-time) hash of contents and returns it as a new wrapped hash.
     */
    public static Sha256Hash create(byte[] contents) {
        return new Sha256Hash(SHA256Hasher.hash(contents));
    }

    /**
     * Calculates the hash of the hash of the contents. This is a standard operation in Bitcoin.
     */
    public static Sha256Hash createDouble(byte[] contents) {
        return new Sha256Hash(SHA256Hasher.hash(SHA256Hasher.hash(contents)));
    }

    /**
     * Returns a hash of the given files contents. Reads the file fully into memory before hashing so only use with
     * small files.
     * @throws IOException
     */
    public static Sha256Hash hashFileContents(File f) throws IOException {
        FileInputStream in = new FileInputStream(f);
        try {
            return create(ByteStreams.toByteArray(in));
        } finally {
            in.close();
        }
    }


    /**
     * Creates a new instance that wraps the given hash value (represented as a hex string).
     *
     * @param hexString a hash value represented as a hex string
     * @return a new instance
     * @throws IllegalArgumentException if the given string is not a valid
     *         hex string, or if it does not represent exactly 32 bytes
     */
    public static Sha256Hash wrap(String hexString) {
        return wrap(Hex.decode(hexString));
    }


    public static Sha256Hash wrap(byte[] convertFromString) {
    	return new Sha256Hash(convertFromString);
	}


	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Arrays.equals(bytes, ((Sha256Hash)o).bytes);
    }

    /**
     * Returns the last four bytes of the wrapped hash. This should be unique enough to be a suitable hash code even for
     * blocks, where the goal is to try and get the first bytes to be zeros (i.e. the value as a big integer lower
     * than the target value).
     */
    @Override
    public int hashCode() {
        // Use the last 4 bytes, not the first 4 which are often zeros in Bitcoin.
        return ByteArray.convertToInt(Arrays.copyOfRange(bytes, 28, 32));
    }

    @Override
    public String toString() {
        return Hex.encode(bytes);
    }

    /**
     * Returns the bytes interpreted as a positive integer.
     */
    public BigInteger toBigInteger() {
        return new BigInteger(1, bytes);
    }

    /**
     * Returns the internal byte array, without defensively copying. Therefore do NOT modify the returned array.
     */
    public byte[] getBytes() {
        return bytes;
    }

    public Sha256Hash duplicate() {
        return new Sha256Hash(Arrays.copyOf(bytes, bytes.length));
    }

    @Override
    public int compareTo(Sha256Hash o) {
        // note that in this implementation compareTo() is not consistent with equals()
    	return ByteArray.compare(o.getBytes(), this.getBytes());// arbitrary but consistent
    }
}

