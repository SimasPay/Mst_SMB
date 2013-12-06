package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiry;
import com.mfino.fix.CmFinoFIX.CMSettlementOfCharge;

public class CashOutInquiryTest {

	public CFIXMsg getMessage()
	{
		CMCashOutInquiry cashOutInquiry = new CMCashOutInquiry();
		
		cashOutInquiry.setDestMDN("2349849898498");
		cashOutInquiry.setAmount(BigDecimal.valueOf(1000));
		cashOutInquiry.setPin("123456");
		cashOutInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashOutInquiry.setSourceMDN("23498984988488");
		cashOutInquiry.setSourcePocketID(12L);
		cashOutInquiry.setDestPocketID(11L);
		
		cashOutInquiry.setSourceApplication(1);
		cashOutInquiry.setMSPID(1L);
		cashOutInquiry.setServletPath("WinFacadeWeb/SmsServicesServlet");
		cashOutInquiry.setChannelCode("7");
		
		return cashOutInquiry;
	}
}
