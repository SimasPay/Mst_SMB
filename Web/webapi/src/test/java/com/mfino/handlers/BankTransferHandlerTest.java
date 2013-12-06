/**
 * 
 */
package com.mfino.handlers;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.transactionapi.handlers.money.impl.BankMoneyTransferHandlerImpl;
import com.mfino.transactionapi.handlers.money.impl.BankTransferInquiryHandlerImpl;
import com.mfino.result.Result.ResultType;
import com.mfino.result.XMLResult;

/**
 * @author Deva
 *
 */
public class BankTransferHandlerTest {
	
	@BeforeClass
	public static void setUp() {
		MfinoServiceProviderDAO mfinoServiceProviderDAO = new MfinoServiceProviderDAO();
		FIXMessageHandler.msp = mfinoServiceProviderDAO.getById(1L);
	}
	
	@Test
	public void testBankTransfer() {
	/*    ChannelCode channelCode = getChannelCode("2002");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BankTransferInquiryHandler bankTransferInqHandler = new BankTransferInquiryHandler(channelCode,"62881123321","123456","62881998919",new BigDecimal(5000), "Unit Test","1","2","Bank");
		bankTransferInqHandler.setResultType(ResultType.XML);
		XMLResult xmlResult1 = (XMLResult) bankTransferInqHandler.handle();
		xmlResult1.setWriter(baos);
		ResponseXMLValidator validator = null;
		try {
			xmlResult1.render();
			validator = new ResponseXMLValidator(72, null, baos.toString());
			validator.isValidOutput();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println(validator.getResponseXML());
		System.err.println();
		BankMoneyTransferHandler bankTransferHandler = new BankMoneyTransferHandler(channelCode,"62881998919","62881123321",validator.getTransferId(),true, -1L,"1","2");
		bankTransferHandler.setResultType(ResultType.XML);
		XMLResult xmlResult = (XMLResult) bankTransferHandler.handle();
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
