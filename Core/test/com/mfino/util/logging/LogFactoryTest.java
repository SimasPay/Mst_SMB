package com.mfino.util.logging;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFactoryTest {
	private static final Logger log = LoggerFactory.getLogger(LogFactoryTest.class);
	
	@Test
	public void testGetLogger(){
		Logger log = LoggerFactory.getLogger(LogFactoryTest.class);
		Assert.assertEquals("com.mfino.util.logging.LogFactoryTest", log.getName());
	}
	
	@Test
	public void testStaticGetLogger(){
		Assert.assertEquals("com.mfino.util.logging.LogFactoryTest", log.getName());
	}
}
