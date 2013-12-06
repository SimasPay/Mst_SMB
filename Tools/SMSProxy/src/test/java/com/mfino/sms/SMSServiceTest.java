/**
 * 
 */
package com.mfino.sms;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.service.SMSService;
/**
 * @author Deva
 *
 */
public class SMSServiceTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.mfino.sms.SMSService#send()}.
	 */
	@Test
	public void testSend() {
		SMSService service = new SMSService();
		service.setDestinationMDN("62123456");
		service.setSourceMDN("123456");
		service.setMessage("Hello from mFino...");
		assertEquals(true, service.send());
		
		service.setDestinationMDN("628818166378");
		service.setSourceMDN("817");
		service.setMessage("Test");
		service.setSmsc("m8_817");
		service.setAccessCode("bank");
	}

	
}
