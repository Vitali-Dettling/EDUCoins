package educoins.core.utils;

import org.educoins.core.cryptography.SHA256Hasher;
import org.educoins.core.utils.Sha256Hash;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by dacki on 12.11.15.
 */
public class Sha256HashTest {
    @Test
    public void testWrapRawHashBytes() {
        byte[] bytes = new byte[32]; // initialized with zero
        Sha256Hash hash = Sha256Hash.wrap(bytes);
        String expected = "0000000000000000000000000000000000000000000000000000000000000000";
        assertEquals(expected, hash.toString());
    }

    @Test
    public void testWrapHexString() {
        String hexString = "0000000000000000000000000000000000000000000000000000000000000000";
        Sha256Hash hash = Sha256Hash.wrap(hexString);
        byte[] expected = new byte[32]; // initialized with zero
        byte[] actual = hash.getBytes();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testCreate() {
        byte[] bytes = new byte[32];
        Sha256Hash hash = Sha256Hash.create(bytes);
        Sha256Hash expected = Sha256Hash.wrap(SHA256Hasher.hash(bytes));
        assertEquals(expected, hash);
    }

    @Test
    public void testCreateDouble() {
        byte[] bytes = new byte[32];
        Sha256Hash hash = Sha256Hash.createDouble(bytes);
        Sha256Hash expected = Sha256Hash.wrap(SHA256Hasher.hash(SHA256Hasher.hash(bytes)));
        assertEquals(expected, hash);
    }
}
