package educoins.core.test;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Sha256Hash;
import org.junit.*;
import org.educoins.core.*;

import java.lang.reflect.Field;

public class BlockTest{
    @Test
    public void BitsSetterTest() {
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffff"); //32
        byte[] expectedCompact = ByteArray.convertFromString("1dffffff");
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        try {
            Field f = b.getClass().getDeclaredField("bits"); //NoSuchFieldException
            f.setAccessible(true);
            byte[] compactBits = (byte[]) f.get(b);
            Assert.assertArrayEquals(expectedCompact, compactBits);
        }
        catch (NoSuchFieldException | IllegalAccessException ex){
            Assert.fail();
        }
    }

    @Test
    public void BitsSetter64Test() {
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"); //64
        byte[] expectedCompact = ByteArray.convertFromString("3dffffff");
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        try {
            Field f = b.getClass().getDeclaredField("bits"); //NoSuchFieldException
            f.setAccessible(true);
            byte[] compactBits = (byte[]) f.get(b);
            Assert.assertArrayEquals(expectedCompact, compactBits);
        }
        catch (NoSuchFieldException | IllegalAccessException ex){
            Assert.fail();
        }
    }

    @Test
    public void BitsGetterTest(){
        byte[] input = ByteArray.convertFromString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        byte[] expec = ByteArray.convertFromString("ffffff0000000000000000000000000000000000000000000000000000000000");
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        Assert.assertArrayEquals(expec, b.getBits().getBytes());
    }

    @Test
    public void BitsGetter2Test(){
        byte[] input = ByteArray.convertFromString("7420dffffffffff");
        byte[] expec = ByteArray.convertFromString("7420d0000000000");
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        byte[] bits = b.getBits().getBytes();
        Assert.assertArrayEquals(expec, bits);
    }

    @Test
    public void BitsGetter3Test(){
        byte[] input = ByteArray.convertFromString("fffffffffffffffffff");
        byte[] expec = ByteArray.convertFromString("fffff00000000000000");
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        Assert.assertArrayEquals(expec, b.getBits().getBytes());
    }
    
    @Test
    public void BitsGetter4Test(){
        byte[] input = ByteArray.convertFromString("1bc330123456789abcde");
        byte[] expec = ByteArray.convertFromString("1bc33000000000000000");
        Block b = new Block();
        b.setBits(Sha256Hash.wrap(input));
        Assert.assertArrayEquals(expec, b.getBits().getBytes());
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
}

