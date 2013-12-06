package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;

public class CashInInquiryTest {

	public CFIXMsg getMessage()
	{
		CMCashInInquiry cashInInquiry = new CMCashInInquiry();
		
		cashInInquiry.setDestMDN("23498984988488");
		cashInInquiry.setAmount(BigDecimal.valueOf(1000));
		cashInInquiry.setPin("123456");
		cashInInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashInInquiry.setSourceMDN("2349849898498");
		cashInInquiry.setSourcePocketID(11L);
		cashInInquiry.setDestPocketID(12L);
		
		cashInInquiry.setSourceApplication(1);
		cashInInquiry.setMSPID(1L);
		cashInInquiry.setServletPath("WinFacadeWeb/SmsServicesServlet");
		cashInInquiry.setChannelCode("7");
		
		return cashInInquiry;
	}
}
