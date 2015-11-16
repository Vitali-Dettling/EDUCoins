package org.educoins.core.utils;



import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

import org.educoins.core.utils.ByteArray;

/**
 * A Sha256Hash just wraps a byte[] so that equals and hashcode work correctly, allowing it to be used as keys in a
 * map. It also checks that the length is correct and provides a bit more type safety.
 */
public class Sha256Hash implements Serializable, Comparable<Sha256Hash> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9086690286714208838L;
	private final byte[] bytes;
    public static final Sha256Hash ZERO_HASH = wrap(new byte[32]);

    

    public Sha256Hash(byte[] bytes) {
		this.bytes = bytes;
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
        return wrap(ByteArray.convertFromString(hexString));
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
        return ByteArray.convertToString(bytes);
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

    //TODO testcase
    @Override
    public int compareTo(Sha256Hash o) {
        // note that in this implementation compareTo() is not consistent with equals()
    	return ByteArray.compare(o.getBytes(), this.getBytes());// arbitrary but consistent
    }
}

