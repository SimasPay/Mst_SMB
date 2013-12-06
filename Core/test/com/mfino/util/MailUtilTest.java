/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.service.impl.MailServiceImpl;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class MailUtilTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Test
    public void testSendMail() {
        String toAddress = "donotreply.mfino@gmail.com";
        String subject = "Test"  + System.currentTimeMillis();
        String msg = "Hello from testing";
        try {
        	MailServiceImpl mailServiceImpl = new MailServiceImpl();
            mailServiceImpl.sendMailMultiX(toAddress, "", subject, msg);
            assert(true);
        } catch(Exception e) {
            log.error("Error in send mail test", e);
            assert(false);
        }
    }
//    @Test
//    public void sendsms(){
//    	
//    	for(int i=0;i<1000;i++){
//    		SMSService service=new SMSService();
//    	service.setDestinationMDN(""+i);
//    	service.setMessage("to"+i);
//    	try{
//    	service.asyncSendSMS();
//    	}catch (Exception e) {
//			log.error("exception",e);
//		}
//    	}
  	
//    }
}
