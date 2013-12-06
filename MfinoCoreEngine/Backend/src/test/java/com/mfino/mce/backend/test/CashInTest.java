package com.mfino.mce.backend.test;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashIn;

public class CashInTest {

	public CFIXMsg getMessage()
	{
		CMCashIn cashIn = new CMCashIn();
		
		cashIn.setSourceMDN("2349849898498");
		cashIn.setDestMDN("23498984988488");
		cashIn.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashIn.setTransferID(47L);
		cashIn.setConfirmed(true);
		cashIn.setParentTransactionID(106L);
		cashIn.setSourcePocketID(11L);
		cashIn.setDestPocketID(12L);
		
		cashIn.setSourceApplication(1);
		cashIn.setMSPID(1L);
		cashIn.setServletPath("WinFacadeWeb/SmsServicesServlet");
		cashIn.setChannelCode("7");
		
		return cashIn;
	}
}
