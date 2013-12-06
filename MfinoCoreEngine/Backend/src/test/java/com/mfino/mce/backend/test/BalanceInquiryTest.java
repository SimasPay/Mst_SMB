package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.mce.core.MCEMessage;

public class BalanceInquiryTest {
	public CFIXMsg getMessage()
	{
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
		
		return balanceInquiry;
	}
	
	public MCEMessage getMceMessage(){
		System.out.println("fromBankTest() ");
		
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
		
		CMBalanceInquiryToBank toBankFix = new CMBalanceInquiryToBank();
//		toBankFix.copy(balanceInquiry);
		toBankFix.setSourceMDN("2348100541926");
		toBankFix.setBankCode(152);
		toBankFix.setPin("1234");
		toBankFix.setSourceCardPAN("34534565555433");
		toBankFix.setPocketID(4L);
		
		toBankFix.setTransactionID(38L);
		toBankFix.setParentTransactionID(38L);
		toBankFix.setSourceBankAccountType(""+CmFinoFIX.BankAccountType_Saving);
		
		CMBalanceInquiryFromBank fromBank  = new CMBalanceInquiryFromBank();
		fromBank.copy(toBankFix);
		fromBank.setResponseCode("00");
		
		fromBank.setSourceApplication(1);
		fromBank.setMSPID(1L);
		fromBank.setServletPath("WinFacadeWeb/SmsServicesServlet");
		fromBank.setChannelCode("7");
		
		System.out.println("bank inquiry test");
		
		CGEntries[] entries = fromBank.allocateEntries(1);
		
		System.out.println("entries="+entries);
		
		entries[0] = new CmFinoFIX.CMBalanceInquiryFromBank.CGEntries();
		
		entries[0].setAmount(BigDecimal.valueOf(3000));
		entries[0].setCurrency(CmFinoFIX.Currency_NGN);
		entries[0].setBankAccountType(CmFinoFIX.BankAccountType_Saving);
		
		fromBank.setEntries(entries);
		
		
		
		mceMessage.setRequest(toBankFix);
		mceMessage.setResponse(fromBank);
		
		return mceMessage;
	}
}
