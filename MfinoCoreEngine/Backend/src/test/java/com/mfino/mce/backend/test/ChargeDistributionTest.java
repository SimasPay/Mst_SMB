package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;

public class ChargeDistributionTest {

	public CFIXMsg getMessage()
	{
		CMChargeDistribution chargeDistribution = new CMChargeDistribution();
		chargeDistribution.setDestMDN("2349849898498");  //agent mdn
		chargeDistribution.setAmount(BigDecimal.valueOf(2000));
		chargeDistribution.setDestPocketID(7L); //agent collector pocket
		
		chargeDistribution.setTransactionChargeID(100L); 
		chargeDistribution.setIsPartOfSharedUpChain(true);
		
		chargeDistribution.setSourceApplication(1);
		chargeDistribution.setMSPID(1L);
		chargeDistribution.setServletPath("WinFacadeWeb/SmsServicesServlet");
		chargeDistribution.setChannelCode("7");
		
		return chargeDistribution;
	}
}
