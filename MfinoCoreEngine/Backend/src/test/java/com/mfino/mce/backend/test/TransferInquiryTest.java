package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.core.MCEMessage;

public class TransferInquiryTest {

	public CFIXMsg getMessage()
	{
		CMBankAccountToBankAccount transferInquiry = new CMBankAccountToBankAccount();
		
		transferInquiry.setDestMDN("2349848740655");
		transferInquiry.setAmount(BigDecimal.valueOf(1000));
		transferInquiry.setPin("1234");
		transferInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferInquiry.setSourceMDN("2349849833820");
		transferInquiry.setSourcePocketID(13L);
		transferInquiry.setDestPocketID(12L);
		
		transferInquiry.setSourceApplication(1);
		transferInquiry.setMSPID(1L);
		transferInquiry.setServletPath("WinFacadeWeb/SmsServicesServlet");
		transferInquiry.setChannelCode("7");
		
		return transferInquiry;
	}
	
	public MCEMessage getMceMessage(){
		MCEMessage mceMessage = new MCEMessage();
		
		CMTransferInquiryToBank toBank = new CMTransferInquiryToBank();
		
		toBank.setAmount(BigDecimal.valueOf(1000));
		toBank.setSourceMDN("2349848740655");
		toBank.setDestMDN("2349849833820");
		toBank.setTransferID(5L);
		toBank.setParentTransactionID(104L);
		toBank.setDestCardPAN("");
//		toBank.InquiryUICategory();
		toBank.setSourcePocketID(13L);
		toBank.setDestPocketID(12L);		
		toBank.setSourceBankAccountType(""+CmFinoFIX.BankAccountType_Saving);
		toBank.setDestinationBankAccountType(""+CmFinoFIX.BankAccountType_Saving);
		
		toBank.setSourceApplication(1);
		toBank.setMSPID(1L);
		toBank.setServletPath("WinFacadeWeb/SmsServicesServlet");
		toBank.setChannelCode("7");
		
		CMTransferInquiryFromBank fromBank = new CMTransferInquiryFromBank();
		
		fromBank.setResponseCode("00");
		fromBank.setAIR("abc");
		
		fromBank.setSourceApplication(1);
		fromBank.setMSPID(1L);
		fromBank.setServletPath("WinFacadeWeb/SmsServicesServlet");
		fromBank.setChannelCode("7");
		
		mceMessage.setRequest(toBank);
		mceMessage.setResponse(fromBank);
		
		return mceMessage;
	}
}
