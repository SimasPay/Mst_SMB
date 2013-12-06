package com.mfino.mce.backend.test;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashOut;

public class CashOutTest {
	
	public CFIXMsg getMessage()
	{
		CMCashOut cashIn = new CMCashOut();
		
		cashIn.setSourceMDN("23498984988488");
		cashIn.setDestMDN("2349849898498");
		cashIn.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashIn.setTransferID(49L);
		cashIn.setConfirmed(true);
		cashIn.setParentTransactionID(114L);
		cashIn.setSourcePocketID(12L);
		cashIn.setDestPocketID(11L);
		
		cashIn.setSourceApplication(1);
		cashIn.setMSPID(1L);
		cashIn.setServletPath("WinFacadeWeb/SmsServicesServlet");
		cashIn.setChannelCode("7");
		
		return cashIn;
	}
}
