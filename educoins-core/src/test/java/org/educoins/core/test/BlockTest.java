package org.educoins.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.educoins.core.Block;
import org.educoins.core.test.utils.Generator;
import org.educoins.core.utils.ByteArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BlockTest{
	
    private static final int ZIRO = 0;
	private static final int FIRST_INDEX = 0;
	
	/*
	 * Example:
	 * https://bitcoin.org/en/developer-reference#merkle-trees
	 * 
	 * Calculation
	 * 0x181bc330 -> 0x1bc330 * 256 ^ (0x18 - 3)
	 * 
	 * Return:
	 * 1b c3 30 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
	 * */
	private static final String EXACT_EXAMPEL = "181bc330";
	private static final String EXPECTED_EXAMPEL = "1bc330000000000000000000000000000000000000000000000000";
		
	private Block block;	
	
	
    @Test
    public void testBitsSetter() {
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
    public void testBitsSetter64() {
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
    public void testBitsGetter(){
        String input = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
        String expec = "ffffff0000000000000000000000000000000000000000000000000000000000";
        Block b = new Block();
        b.setBits(input);
        Assert.assertEquals(expec, b.getBits());
    }

    @Test
    public void testBitsGetter2(){
        String input = "7420dffffffffff";
        String expec = "7420df000000000";
        Block b = new Block();
        b.setBits(input);
        Assert.assertEquals(expec, b.getBits());
    }

    @Test
    public void testBitsGetter3(){
        String input = "fffffffffffffffffff";
        String expec = "ffffff0000000000000";
        Block b = new Block();
        b.setBits(input);
        Assert.assertEquals(expec, b.getBits());
    }

    @Test
    public void testBitsAsByte(){
        String input = "ffffffffffffffffffffffffffffffff";
        String expec = "ffffff00000000000000000000000000";
        Block b = new Block();
        b.setBits(input);
        Assert.assertEquals(expec, b.getBits());
        byte[] byteArray = Block.getTargetThreshold(expec);
        Assert.assertArrayEquals(new byte[]{ -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0,0, 0, 0, 0, 0 }, byteArray);
    }
    
	
	private Generator randomNumber;

	@Before
	public void setUp(){
		this.randomNumber = new Generator();
	}
	
	@Test
	@SuppressWarnings("static-access")
	public void testgetTargetThreshold(){
		
		byte[] exactTestee = this.block.getTargetThreshold(EXACT_EXAMPEL);
		byte[] resultTestee = ByteArray.convertFromString(EXPECTED_EXAMPEL);
		
		assertNotNull(exactTestee);
		assertNotNull(resultTestee);

		//EXACT_EXAMPEL should be converted into RESULT_EXAMPEL as one can see at the top of the class. 
		assertEquals(exactTestee, resultTestee);		
	}
	
	@Test
	@SuppressWarnings("static-access")
	public void testTargetThresholdNotNegative(){
		
		String randomNumber = this.randomNumber.getSecureRandomString256HEX(); 
		byte[] rantomTestee = this.block.getTargetThreshold(randomNumber);
		
		assertNotNull(rantomTestee);
		//The returning byte array should never be negative. 
		assertTrue("First number should not be ziro: ", rantomTestee[FIRST_INDEX] >= ZIRO);		
	}
	
}

