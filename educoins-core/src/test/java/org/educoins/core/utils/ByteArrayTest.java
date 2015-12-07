package org.educoins.core.utils;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayTest {
    @Test
    public void compare1Test() {
        byte[] a = new byte[]{ 4, 5, 6};
        byte[] b = new byte[]{ 3, 5, 6};
        Assert.assertEquals(1, ByteArray.compare(a, b));
        Assert.assertEquals(-1, ByteArray.compare(b, a));
    }

    @Test
    public void compareEqualsTest() {
        byte[] a = new byte[]{ 4, 5, 6};
        byte[] b = new byte[]{ 4, 5, 6};
        Assert.assertEquals(0, ByteArray.compare(a, b));
        Assert.assertEquals(0, ByteArray.compare(b, a));
    }

    @Test
    public void compareDiffLengthTest() {
        byte[] a = new byte[]{ 0,0,0,0, 5, 6};
        byte[] b = new byte[]{ 4, 5, 6};
        Assert.assertEquals(-1, ByteArray.compare(a, b));
        Assert.assertEquals(1, ByteArray.compare(b, a));
    }
    
    @Test
    public void compareTest2() {
        byte[] a = new byte[]{-1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] b = new byte[]{-96, -77, 91, 35, -104, -87, 33, -67, 113, 62, 112, -25, 50, 83, -55, -5, -7, 102, -74, 87, -18, 57, -97, -18, -119, -22, -66, -27, -35, 45, -122, -41};
        Assert.assertEquals(1, ByteArray.compare(a, b));
        Assert.assertEquals(-1, ByteArray.compare(b, a));
    }
    
    @Test
    public void compareTest3() {
    	//0x1d00ffff
        byte[] defaultDiff = new byte[]{-1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] comp = new byte[]{-79, 125, 78, -104, -9, 48, -20, -99, -91, 107, -9, 16, -95, -2, -51, -15, -31, 119, 70, 63, -64, -57, 17, -3, -95, 0, 48, -123, 46, 63, -6, -3};
        
        byte[] a1 = ByteArray.convertFromString("01003456");
        byte[] a2 = ByteArray.convertFromString("1d00ffff");
        
        Assert.assertEquals(1, ByteArray.compare(defaultDiff, comp));
        Assert.assertEquals(-1, ByteArray.compare(comp, defaultDiff));
    }
    
    @Test
    public void compareTest4() {
        byte[] a = new byte[]{112, -25, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] b = new byte[]{-34, -108, -128, 73, 68, -96, 115, -85, 38, 56, 64, 94, 15, -99, 45, 4, 98, 77, 110, -69, -77, 1, 78, -51, -68, -59, 59, 24, -77, 83, -28, 15};
        
        Assert.assertEquals(-1, ByteArray.compare(a, b));
        Assert.assertEquals(1, ByteArray.compare(b, a));
    }
    
}
