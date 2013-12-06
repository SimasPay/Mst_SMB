package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPurchaseInquiry;

public class PurchasetInquiryTest {

	public CFIXMsg getMessage()
	{
		CMPurchaseInquiry purchaseInquiry = new CMPurchaseInquiry();
		
		purchaseInquiry.setDestMDN("2349849898498");
		purchaseInquiry.setAmount(BigDecimal.valueOf(1000));
		purchaseInquiry.setPin("123456");
		purchaseInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		purchaseInquiry.setSourceMDN("23498984988488");
		purchaseInquiry.setSourcePocketID(12L);
		purchaseInquiry.setDestPocketID(11L);
		
		purchaseInquiry.setSourceApplication(1);
		purchaseInquiry.setMSPID(1L);
		purchaseInquiry.setServletPath("WinFacadeWeb/SmsServicesServlet");
		purchaseInquiry.setChannelCode("7");
		
		return purchaseInquiry;
	}
}
