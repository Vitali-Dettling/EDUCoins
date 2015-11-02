package org.educoins.core;

import static org.junit.Assert.*;

import java.security.SecureRandom;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Generator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class BlockTest {

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
	
	private Generator randomNumber;

	@Before
	public void setUp(){
		this.randomNumber = new Generator();
	}
	
	@Test
	@SuppressWarnings("static-access")
	public void getTargetThresholdTest(){
		
		String randomNumber = this.randomNumber.getSecureRandomString256HEX(); 
		byte[] rantomTestee = this.block.getTargetThreshold(randomNumber);
		String exactNumber = EXACT_EXAMPEL;
		byte[] exactTestee = this.block.getTargetThreshold(exactNumber);
		byte[] resultTestee = ByteArray.convertFromString(EXPECTED_EXAMPEL);
		
		assertNotNull(rantomTestee);
		assertNotNull(exactTestee);
		//The returning byte array should never be negativ. 
		assertTrue("First number should not be ziro: ", rantomTestee[FIRST_INDEX] >= ZIRO);	
		//EXACT_EXAMPEL should be converted into RESULT_EXAMPEL as one can see at the top of the class. 
		assertEquals(exactTestee, resultTestee);		
	}
	
	
	
	
	
	
	 

	 
	 
	 
	 
}
