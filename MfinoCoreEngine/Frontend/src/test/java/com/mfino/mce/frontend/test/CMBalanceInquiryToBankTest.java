package com.mfino.mce.frontend.test;

import java.math.BigDecimal;
import java.util.UUID;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiry;

public class CMBalanceInquiryToBankTest 
{
	public CFIXMsg getMessage()
	{
		CMBalanceInquiryToBank balanceInquiry = new CMBalanceInquiryToBank();
		balanceInquiry.setSourceBankAccountType(""+CmFinoFIX.BankAccountType_Saving);
		balanceInquiry.setSourceCardPAN("2020193315");
		//balanceInquiry.setPOSDataCode("hello");
		//balanceInquiry.setTransactionID(Long.parseLong(Long.toString(UUID.randomUUID().getMostSignificantBits()).substring(0, 6)));
		balanceInquiry.setTransactionID(100001L);
		balanceInquiry.setSourceMDN("2341234567");
		return balanceInquiry;
	}
}
