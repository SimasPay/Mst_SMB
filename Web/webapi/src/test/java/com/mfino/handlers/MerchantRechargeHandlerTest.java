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
public class MerchantRechargeHandlerTest {
	@BeforeClass
	public static void setUp() {
		MfinoServiceProviderDAO mfinoServiceProviderDAO = new MfinoServiceProviderDAO();
		FIXMessageHandler.msp = mfinoServiceProviderDAO.getById(1L);
	}
	@Test
	public void testCheckBalance() {
//	    ChannelCode channelCode = getChannelCode("2002");
//
//		MerchantTopupHandlerImpl merchantRechargeHandler = new MerchantTopupHandlerImpl(channelCode, "6288116210961", "123456", "6288116210998", new BigDecimal(5000), CmFinoFIX.BucketType_Recharge_Regular);
//		merchantRechargeHandler.setResultType(ResultType.XML);
//		XMLResult xmlResult = (XMLResult) merchantRechargeHandler.handle();
//		xmlResult.setWriter(System.err);
//		
//		try {
//			xmlResult.render();
//			System.err.println();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	@AfterClass
	public static void tearDown() {
	}
	private ChannelCode getChannelCode(String channelIdStr) {
		ChannelCodeDAO channelCodeDAO = new ChannelCodeDAO();
		return channelCodeDAO.getByChannelCode(channelIdStr);
	}
}
