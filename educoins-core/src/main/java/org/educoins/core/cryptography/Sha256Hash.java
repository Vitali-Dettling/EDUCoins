package org.educoins.core.cryptography;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;


/**
 * A Sha256Hash just wraps a byte[] so that equals and hashcode work correctly, allowing it to be used as keys in a
 * map. It also checks that the length is correct and provides a bit more type safety.
 */
public class Sha256Hash implements Serializable, Comparable<Sha256Hash> {
    static BaseEncoding Hex = BaseEncoding.base16().lowerCase();

    private final byte[] bytes;
    public static final Sha256Hash ZERO_HASH = new Sha256Hash(new byte[32]);

    /**
     * Creates a Sha256Hash by wrapping the given byte array. It must be 32 bytes long.
     */
    protected Sha256Hash(byte[] rawHashBytes) {
        checkArgument(rawHashBytes.length == 32);
        this.bytes = rawHashBytes;

    }

    /**
     * Creates a Sha256Hash by decoding the given hex string. It must be 64 characters long.
     */
    protected Sha256Hash(String hexString) {
        checkArgument(hexString.length() == 64);
        this.bytes = Hex.decode(hexString);
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
     * Wraps an existing hash from its byte representation
     * @param rawHashBytes
     * @return Wrapped hash
     */
    public static Sha256Hash wrap(byte[] rawHashBytes) {
        return new Sha256Hash(rawHashBytes);
    }

    /**
     *
     * @param hexString
     * @return Wrapped hash
     */
    public static Sha256Hash wrap(String hexString) {
        return new Sha256Hash(hexString);
    }

    /**
     * Returns true if the hashes are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Sha256Hash)) return false;
        return Arrays.equals(bytes, ((Sha256Hash) other).bytes);
    }

    /**
     * Hash code of the byte array as calculated by {@link Arrays#hashCode()}. Note the difference between a SHA256
     * secure bytes and the type of quick/dirty bytes used by the Java hashCode method which is designed for use in
     * bytes tables.
     */
    @Override
    public int hashCode() {
        // Use the last 4 bytes, not the first 4 which are often zeros in Bitcoin.
        return (bytes[31] & 0xFF) | ((bytes[30] & 0xFF) << 8) | ((bytes[29] & 0xFF) << 16) | ((bytes[28] & 0xFF) << 24);
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
     * Returns the byte representation of the hash
     */
    public byte[] getBytes() {
        return bytes;
    }

    public Sha256Hash duplicate() {
        return new Sha256Hash(bytes);
    }

    @Override
    public int compareTo(Sha256Hash o) {
        checkArgument(o instanceof Sha256Hash);
        int thisCode = this.hashCode();
        int oCode = ((Sha256Hash)o).hashCode();
        return thisCode > oCode ? 1 : (thisCode == oCode ? 0 : -1);
    }
}