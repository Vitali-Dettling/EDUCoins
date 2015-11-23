package org.educoins.core.utils;

import org.educoins.core.utils.ByteArray;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Marvin on 29.10.2015.
 */
public class ByteArrayTest {
    @Test
    public void testCompare1() {
        byte[] a = new byte[]{ 4, 5, 6};
        byte[] b = new byte[]{ 3, 5, 6};
        Assert.assertEquals(1, ByteArray.compare(a, b));
        Assert.assertEquals(-1, ByteArray.compare(b, a));
    }

    @Test
    public void testCompareEquals() {
        byte[] a = new byte[]{ 4, 5, 6};
        byte[] b = new byte[]{ 4, 5, 6};
        Assert.assertEquals(0, ByteArray.compare(a, b));
        Assert.assertEquals(0, ByteArray.compare(b, a));
    }

    @Test
    public void testCompareDiffLength() {
        byte[] a = new byte[]{ 0,0,0,0, 5, 6};
        byte[] b = new byte[]{ 4, 5, 6};
        Assert.assertEquals(-1, ByteArray.compare(a, b));
        Assert.assertEquals(1, ByteArray.compare(b, a));
    }
}
