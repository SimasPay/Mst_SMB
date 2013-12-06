package com.mfino.bsim.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class BillPaymentInquiryFromBankProcessor implements BSIMISOtoFixProcessor{
	
	@Override
    public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMBSIMBillPaymentInquiryToBank toBank = (CMBSIMBillPaymentInquiryToBank)request;
		CMBSIMBillPaymentInquiryFromBank fromBank = new CMBSIMBillPaymentInquiryFromBank();
		
		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
		
		fromBank.copy(toBank);
		if(isoMsg.hasField(3))
			fromBank.setProcessingCodeDE3(isoMsg.getString(3).substring(4, 6));
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		fromBank.setInfo1(isoMsg.getString(61));
		fromBank.setInfo3(isoMsg.getString(62));
		if(isoMsg.hasField(63)){
			fromBank.setServiceChargeDE63(isoMsg.getString(63));
		}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		
	    return fromBank;
    }
}
