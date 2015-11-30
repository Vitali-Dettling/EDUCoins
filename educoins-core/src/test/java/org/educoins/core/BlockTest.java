package org.educoins.core;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;
import org.junit.Assert;
import org.junit.Test;

public class BlockTest{

    @Test
    public void BitsSetterTest() {
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffff"); //32
        byte[] expectedCompact = ByteArray.convertFromString("0dffffff");
        Assert.assertArrayEquals(expectedCompact, getCompactBits(input));
    }

    @Test
    public void BitsSetter64Test() {
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"); //64
        byte[] expectedCompact = ByteArray.convertFromString("1dffffff");
        Assert.assertArrayEquals(expectedCompact, getCompactBits(input));
    }

    private byte[] getCompactBits(byte[] input) {
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        byte[] compactBits = null;
        try {
            Field f = b.getClass().getDeclaredField("bits"); //NoSuchFieldException
            f.setAccessible(true);
            compactBits = (byte[]) f.get(b);
        }
        catch (NoSuchFieldException | IllegalAccessException ex){
            Assert.fail();
        }
        return compactBits;
    }

    @Test
    public void BitsGetterTest(){
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        byte[] expec = ByteArray.convertFromString("ffffff0000000000000000000000000000000000000000000000000000000000");
        Assert.assertArrayEquals(expec, getBits(input));
    }

    @Test
    public void BitsGetter2Test(){
        byte[] input = ByteArray.convertFromString("7420dffffffffff");
        byte[] expec = ByteArray.convertFromString("7420d0000000000");
        Assert.assertArrayEquals(expec, getBits(input));
    }

    @Test
    public void BitsGetter3Test(){
        byte[] input = ByteArray.convertFromString("fffffffffffffffffff");
        byte[] expec = ByteArray.convertFromString("fffff00000000000000");
        Assert.assertArrayEquals(expec, getBits(input));
    }
    
    @Test
    public void BitsGetter4Test(){
        byte[] input = ByteArray.convertFromString("1bc330123456789abcde");
        byte[] expec = ByteArray.convertFromString("1bc33000000000000000");
        Assert.assertArrayEquals(expec, getBits(input));
    }

    private byte[] getBits(byte[] input) {
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        return b.getBits().getBytes();
    }

    @Test
    public void BitsAsByte(){
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffff");
        byte[] expec = ByteArray.convertFromString("ffffff00000000000000000000000000");
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        Assert.assertArrayEquals(expec, b.getBits().getBytes());
        Assert.assertArrayEquals(new byte[]{ -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0,0, 0, 0, 0, 0 }, b.getBits().getBytes());
    }

    @Test
    public void calculateMerkleRootTest() {
        List<Transaction> list = new LinkedList<>();
        for (int i = 1; i < 6; i++) {
            list.add(BlockStoreFactory.generateTransaction(i));
        }
        Block b = new Block();
        b.addTransactions(list);
        Assert.assertArrayEquals(b.getHashMerkleRoot().getBytes(), Sha256Hash.wrap("5e9dc0cb197e89a155683decb4473848e50530183845119ddd8f9d361dddea8a").getBytes());
    }

    @Test
    public void calculateMerkleRootTest2() {
        List<Transaction> list = new LinkedList<>();
        for (int i = 20; i < 40; i++) {
            list.add(BlockStoreFactory.generateTransaction(i));
        }
        Block b = new Block();
        b.addTransactions(list);
        Assert.assertArrayEquals(b.getHashMerkleRoot().getBytes(), Sha256Hash.wrap("c76bc870259e380e3b7ba45ef97123df9182a53af6eb6a87fe13e06274b2532e").getBytes());
    }
}

