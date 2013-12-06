package com.mfino.mce.backend.test;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.MCEMessage;

public class TransferConfirmationToBank {

	public CFIXMsg getMessage()
	{
		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		
		transferConfirmation.setSourceMDN("2349849833820");
		transferConfirmation.setDestMDN("2349848740655");
		
		transferConfirmation.setTransferID(5L);
		transferConfirmation.setConfirmed(true);
		transferConfirmation.setParentTransactionID(104L);
		transferConfirmation.setTransactionID(5L);	
		transferConfirmation.setSourcePocketID(13L);
		transferConfirmation.setDestPocketID(12L);
		
		transferConfirmation.setSourceApplication(1);
		transferConfirmation.setMSPID(1L);
		transferConfirmation.setServletPath("WinFacadeWeb/SmsServicesServlet");
		transferConfirmation.setChannelCode("7");
		
		return transferConfirmation;
	}
	
	public MCEMessage getMceMessage(){
		CMMoneyTransferToBank moneyTransferToBank = new CMMoneyTransferToBank();
//		moneyTransferToBank.copy(confirmationToBank);
		
		moneyTransferToBank.setSourceMDN("2349848740655");
		moneyTransferToBank.setAmount(BigDecimal.valueOf(1000));
		moneyTransferToBank.setBankCode(1099);
		moneyTransferToBank.setDestCardPAN("34098730956");
		moneyTransferToBank.setDestMDN("2349849833820");
		moneyTransferToBank.setParentTransactionID(104L);
//		moneyTransferToBank.setUICategory(pct.getUICategory());
		moneyTransferToBank.setSourcePocketID(13L);
		moneyTransferToBank.setDestPocketID(12L);
//		moneyTransferToBank.setPin(MCEUtil.FAKE_PIN_FOR_OMB);
		moneyTransferToBank.setSourceCardPAN("0098487898155604");
		moneyTransferToBank.setTransferID(5L);
		moneyTransferToBank.setTransferTime(new Timestamp(System.currentTimeMillis()));
		
		moneyTransferToBank.setSourceApplication(1);
		moneyTransferToBank.setMSPID(1L);
		moneyTransferToBank.setServletPath("WinFacadeWeb/SmsServicesServlet");
		moneyTransferToBank.setChannelCode("7");
		
		CMMoneyTransferFromBank fromBank = new CMMoneyTransferFromBank();
		
		fromBank.setResponseCode("00");
		fromBank.setAIR("abc");
		
		fromBank.setSourceApplication(1);
		fromBank.setMSPID(1L);
		fromBank.setServletPath("WinFacadeWeb/SmsServicesServlet");
		fromBank.setChannelCode("7");
		
		MCEMessage mceMessage = new MCEMessage();
		mceMessage.setRequest(moneyTransferToBank);
		mceMessage.setResponse(fromBank);
		
		return mceMessage;
	}
}
