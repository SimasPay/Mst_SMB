package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSettlementOfCharge;

public class SettlementOfChargeTest {

	public CFIXMsg getMessage()
	{
		CMSettlementOfCharge settlementOfCharge = new CMSettlementOfCharge();
		
		settlementOfCharge.setSourcePocketID(7L);
		settlementOfCharge.setDestPocketID(11L); // settlement pocket for agent.
		settlementOfCharge.setSourceMDN("2349849898498");
		settlementOfCharge.setAmount(BigDecimal.valueOf(10000));
		
		settlementOfCharge.setSourceApplication(1);
		settlementOfCharge.setMSPID(1L);
		settlementOfCharge.setServletPath("WinFacadeWeb/SmsServicesServlet");
		settlementOfCharge.setChannelCode("7");
		
		return settlementOfCharge;
	}
}
