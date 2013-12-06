package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationCashIn;

public class SubscriberRegistrationCashInTest {
	
	public CFIXMsg getMessage()
	{
		CMSubscriberRegistrationCashIn regCashIn = new CMSubscriberRegistrationCashIn();
		regCashIn.setSourceMDN("2349849898498");
		regCashIn.setDestMDN("2349866060307");
		regCashIn.setAmount(BigDecimal.valueOf(2000));
		regCashIn.setSourcePocketID(7L);
		regCashIn.setDestPocketID(1L);
		regCashIn.setCharges(BigDecimal.valueOf(100));
//		regCashIn.setPassword("C6D4D13EC2D1F04A00095F758C653AB4000F0DB3");
		regCashIn.setPin("123456");
		
		regCashIn.setSourceApplication(1);
		regCashIn.setMSPID(1L);
		regCashIn.setServletPath("WinFacadeWeb/SmsServicesServlet");
		regCashIn.setChannelCode("7");
		
		return regCashIn;
	}
}
