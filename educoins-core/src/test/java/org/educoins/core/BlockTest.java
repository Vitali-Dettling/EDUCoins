package org.educoins.core;

import org.educoins.core.transaction.Transaction;
import org.educoins.core.utils.BlockStoreFactory;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class BlockTest{

    @Test
    public void BitsSetterTest() {
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffff"); //32
        byte[] expectedCompact = ByteArray.convertFromString("0dffffff");
        Assert.assertArrayEquals(expectedCompact, setInputAndGetCompactBits(input));
    }

    @Test
    public void BitsSetter64Test() {
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"); //64
        byte[] expectedCompact = ByteArray.convertFromString("1dffffff");
        Assert.assertArrayEquals(expectedCompact, setInputAndGetCompactBits(input));
    }


    private Field getBitsField(Block b){
        Field f = null;
        try {
            f = b.getClass().getDeclaredField("bits"); //NoSuchFieldException
            f.setAccessible(true);
        }
        catch (NoSuchFieldException ex){
            Assert.fail();
        }
        return f;
    }

    private void setCompactBits(Block b, byte[] input){
        try {
            Field f = getBitsField(b);
            f.set(b, input);
        }
        catch (IllegalAccessException ex){
            Assert.fail();
        }
    }

    private byte[] setInputAndGetCompactBits(byte[] input) {
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        return getCompactBits(b);
    }

    private byte[] getCompactBits(Block b) {
        Field f = getBitsField(b);
        try {
            return (byte[])f.get(b);
        } catch (IllegalAccessException e) {
            Assert.fail();
            return null;
        }
    }

    private byte[] getBits(byte[] input) {
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        return b.getBits().getBytes();
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
            list.add(BlockStoreFactory.generateTransactionWithSameUnlockingScript(i));
        }
        Block b = new Block();
        b.addTransactions(list);
        Assert.assertEquals(Sha256Hash.wrap("a7833eccf6bed7ca41482806151b71c36b2a192d65eaee4317771f1a2669d394"), b.getHashMerkleRoot());
    }

    @Test
    public void calculateMerkleRootTest2() {
        List<Transaction> list = new LinkedList<>();
        for (int i = 20; i < 40; i++) {
            list.add(BlockStoreFactory.generateTransactionWithSameUnlockingScript(i));
        }
        Block b = new Block();
        b.addTransactions(list);
        Assert.assertEquals(Sha256Hash.wrap("daa2fcf60738bc84c8ccbe0e277bcf54cf1038d53b1d82f603e1e1b223ab0e1b"),b.getHashMerkleRoot());
    }

    @Test
    public void getBitsWithLargeExponent(){
        byte[] bits = new byte[] { -1, 1, 78, 0};
        Block b = new Block();
        setCompactBits(b, bits);
        byte[] actual = b.getBits().getBytes();
        Assert.assertEquals(actual[0], 1);
        Assert.assertEquals(actual[1], 78);
        Assert.assertEquals(actual[2], 0);
        Assert.assertEquals(258, actual.length);

    }

}

