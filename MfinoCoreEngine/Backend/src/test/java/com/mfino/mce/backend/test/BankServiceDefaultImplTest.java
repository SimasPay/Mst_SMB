package com.mfino.mce.backend.test;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.mce.backend.impl.BackendServiceDefaultImpl;
import com.mfino.mce.core.MCEMessage;

public class BankServiceDefaultImplTest {
	
	public static void main(String[] args) {
		System.out.println("BackendServiceDefaultImpl Test");
		BackendServiceDefaultImpl backendServiceDefaultImpl = new BackendServiceDefaultImpl();
		
		
		MCEMessage mceMessage = new MCEMessage();
		
		CMBankAccountBalanceInquiry balanceInquiry = new CMBankAccountBalanceInquiry();
		
		balanceInquiry = new CMBankAccountBalanceInquiry();
		balanceInquiry.setPin("1234");
		balanceInquiry.setServletPath(CmFinoFIX.ServletPath_BankAccount);
		balanceInquiry.setSourceMDN("2348100541926");
		balanceInquiry.setPocketID(4L);
		
		balanceInquiry.setSourceApplication(1);
		balanceInquiry.setMSPID(1L);
		balanceInquiry.setServletPath("WinFacadeWeb/SmsServicesServlet");
		balanceInquiry.setChannelCode("7");
		
		mceMessage.setRequest(balanceInquiry);
		
		mceMessage = backendServiceDefaultImpl.processMessage(mceMessage);
		
		
		System.out.println(mceMessage);
	}
}
