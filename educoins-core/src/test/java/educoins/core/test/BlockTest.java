package educoins.core.test;
import junit.framework.TestCase;
import org.junit.*;
import org.educoins.core.*;

import java.lang.reflect.Field;

public class BlockTest{
    @Test
    public void BitsSetterTest() {
        String input = "ffffffffffffffffffffffffffffffff"; //32
        String expectedCompact = "1dffffff";
        Block b = new Block();
        b.setBits(input);
        try {
            Field f = b.getClass().getDeclaredField("bits"); //NoSuchFieldException
            f.setAccessible(true);
            String compactBits = (String) f.get(b);
            Assert.assertEquals(expectedCompact, compactBits);
        }
        catch (NoSuchFieldException ex){
            Assert.fail();
        }
        catch (IllegalAccessException ex){
            Assert.fail();
        }
    }

    @Test
    public void BitsSetter64Test() {
        String input = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"; //64
        String expectedCompact = "3dffffff";
        Block b = new Block();
        b.setBits(input);
        try {
            Field f = b.getClass().getDeclaredField("bits"); //NoSuchFieldException
            f.setAccessible(true);
            String compactBits = (String) f.get(b);
            Assert.assertEquals(expectedCompact, compactBits);
        }
        catch (NoSuchFieldException ex){
            Assert.fail();
        }
        catch (IllegalAccessException ex){
            Assert.fail();
        }
    }

    @Test
    public void BitsGetterTest(){
        String input = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
        String expec = "ffffff0000000000000000000000000000000000000000000000000000000000";
        Block b = new Block();
        b.setBits(input);
        Assert.assertEquals(expec, b.getBits());
    }

    @Test
    public void BitsGetter2Test(){
        String input = "7420dffffffffff";
        String expec = "7420df000000000";
        Block b = new Block();
        b.setBits(input);
        Assert.assertEquals(expec, b.getBits());
    }

    @Test
    public void BitsGetter3Test(){
        String input = "fffffffffffffffffff";
        String expec = "ffffff0000000000000";
        Block b = new Block();
        b.setBits(input);
        Assert.assertEquals(expec, b.getBits());
    }

    @Test
    public void BitsAsByte(){
        String input = "ffffffffffffffffffffffffffffffff";
        String expec = "ffffff00000000000000000000000000";
        Block b = new Block();
        b.setBits(input);
        Assert.assertEquals(expec, b.getBits());
        byte[] byteArray = Block.getTargetThreshold(expec);
        Assert.assertArrayEquals(new byte[]{ -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0,0, 0, 0, 0, 0 }, byteArray);
    }
}

