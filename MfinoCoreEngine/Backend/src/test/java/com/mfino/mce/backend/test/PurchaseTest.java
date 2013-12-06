package com.mfino.mce.backend.test;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPurchase;

public class PurchaseTest {
	
	public CFIXMsg getMessage()
	{
		CMPurchase purchase = new CMPurchase();
		
		purchase.setSourceMDN("23498984988488");
		purchase.setDestMDN("2349849898498");
		purchase.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		purchase.setTransferID(49L);
		purchase.setConfirmed(true);
		purchase.setParentTransactionID(114L);
		purchase.setSourcePocketID(12L);
		purchase.setDestPocketID(11L);
		
		purchase.setSourceApplication(1);
		purchase.setMSPID(1L);
		purchase.setServletPath("WinFacadeWeb/SmsServicesServlet");
		purchase.setChannelCode("7");
		
		return purchase;
	}
}
