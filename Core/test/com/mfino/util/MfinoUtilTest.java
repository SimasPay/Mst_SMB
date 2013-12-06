// Copyright 2010 mFino India. All Rights Reserved.

package com.mfino.util;



import junit.framework.Assert;

import org.junit.Test;

import com.mfino.service.impl.SubscriberServiceImpl;


/**
 * @author sid@mfino.com (Siddhartha Chinthapally)
 *
 */
public class MfinoUtilTest {
	
	SubscriberServiceImpl subscriberServiceImpl = new SubscriberServiceImpl();
  
  @Test
  public void stripMDNTest1() {
    String MDN = "62088112345";
    String expectedMDN = "6288112345";
    String strippedMDN = subscriberServiceImpl.normalizeMDN(MDN);
    System.out.println(strippedMDN);
    Assert.assertEquals(expectedMDN, strippedMDN);
  }
  
  @Test
  public void stripMDNTest2() {
    String MDN = "62000088112345";
    String expectedMDN = "6288112345";
    String strippedMDN = subscriberServiceImpl.normalizeMDN(MDN);
    System.out.println(strippedMDN);
    Assert.assertEquals(expectedMDN, strippedMDN);
  }
  
  @Test
  public void stripMDNTest3() {
    String MDN = "6288112345";
    String expectedMDN = "6288112345";
    String strippedMDN = subscriberServiceImpl.normalizeMDN(MDN);
    System.out.println(strippedMDN);
    Assert.assertEquals(expectedMDN, strippedMDN);
  }
  
  @Test
  public void stripMDNTest4() {
    String MDN = "088112345";
    String expectedMDN = "6288112345";
    String strippedMDN = subscriberServiceImpl.normalizeMDN(MDN);
    System.out.println(strippedMDN);
    Assert.assertEquals(expectedMDN, strippedMDN);
  }
  
  @Test
  public void stripMDNTest5() {
    String MDN = "00088112345";
    String expectedMDN = "6288112345";
    String strippedMDN = subscriberServiceImpl.normalizeMDN(MDN);
    System.out.println(strippedMDN);
    Assert.assertEquals(expectedMDN, strippedMDN);
  }
  
  @Test
  public void stripMDNTest6() {
    String MDN = "0000";
    String expectedMDN = "62";
    String strippedMDN = subscriberServiceImpl.normalizeMDN(MDN);
    System.out.println(strippedMDN);
    Assert.assertEquals(expectedMDN, strippedMDN);
  }
  
  @Test
  public void stripMDNTest7() {
    String MDN = "626262123456";
    String expectedMDN = "62123456";
    String strippedMDN = subscriberServiceImpl.normalizeMDN(MDN);
    System.out.println(strippedMDN);
    Assert.assertEquals(expectedMDN, strippedMDN);
  }
}
