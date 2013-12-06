/**
 * 
 */
package com.mfino.handlers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.domain.ChannelCode;

/**
 * @author Deva
 *
 */
public class BankAccountRechargeHandlerTest {
	@BeforeClass
	public static void setUp() {
		MfinoServiceProviderDAO mfinoServiceProviderDAO = new MfinoServiceProviderDAO();
		FIXMessageHandler.msp = mfinoServiceProviderDAO.getById(1L);
	}
	
	@Test
	public void testCheckBalance() {
	    ChannelCode channelCode = getChannelCode("2002");
	    /*BankAccountTopupHandler baRechargeHandler = new BankAccountTopupHandler(channelCode, "62881123321", "123456", "62881123321", new BigDecimal(5000), CmFinoFIX.BucketType_Call_And_SMS_Bank_Account,"");
		baRechargeHandler.setResultType(ResultType.XML);
		XMLResult xmlResult = (XMLResult) baRechargeHandler.handle();
		xmlResult.setWriter(System.err);
		
		try {
			xmlResult.render();
			System.err.println();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	@AfterClass
	public static void tearDown() {
	}
	private ChannelCode getChannelCode(String channelIdStr) {
		ChannelCodeDAO channelCodeDAO = new ChannelCodeDAO();
		return channelCodeDAO.getByChannelCode(channelIdStr);
	}
}
