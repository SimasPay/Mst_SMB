package com.mfino.iso8583.processor.bank.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.ISinarmasISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public class MoneyTransfer implements ISinarmasISOtoFIXProcessor{

	@Override
    public CFIXMsg process(SinarmasISOMessage isoMsg, CFIXMsg request) throws Exception {
		
		CMMoneyTransferToBank toBank = (CMMoneyTransferToBank)request;
		CMMoneyTransferFromBank fromBank = new CMMoneyTransferFromBank();
		
		if(!CmFinoFIX.ISO8583_ResponseCode_Success.equals(isoMsg.getResponseCode()))
			return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
		
		fromBank.copy(toBank);
		fromBank.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		fromBank.setResponseCode(isoMsg.getResponseCode());
		fromBank.setBankAccountName(isoMsg.getSianrmasBankAccountName());
		
//		fromBank.header().setMsgSeqNum(null);
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		
	    return fromBank;
    }

}
