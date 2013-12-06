package com.mfino.iso8583;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.iso8583.processor.bank.AdditionalAmounts;

import static org.junit.Assert.*;

public class AdditionalAmountsTest{

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		try {
	        AdditionalAmounts aa = AdditionalAmounts.parseAdditionalAmounts("1002360C000000003426");
	        assertEquals(aa.getAccountType(), 10);
	        assertEquals(aa.getAmountType()	, 2);
	        assertEquals(aa.getCurrencyCode(), 360);
	        assertEquals(aa.getAmountSign(), 'C');
	        assertEquals(aa.getAmount(), 3426);
        }
        catch (Exception ex) {
        	fail("invalid string");
        }
	}
	
	@Test
	public void test2() {
		try {
	        AdditionalAmounts aa = AdditionalAmounts.parseAdditionalAmounts("");
        }
        catch (Exception ex) {
        	return;
        }
        fail("test failed");
	}
}
