package com.mfino.bsm.uangku.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.bsm.uangku.iso8583.processor.BSMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class TransferInquiryFromBankProcessor implements BSMISOtoFixProcessor{
	
	@Override
    public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMTransferInquiryToBank toBank = (CMTransferInquiryToBank)request;
		CMTransferInquiryFromBank fromBank = new CMTransferInquiryFromBank();
		
		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
		
		fromBank.copy(toBank);
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		if(isoMsg.hasField(48))
		   fromBank.setBankAccountName(isoMsg.getString(48));
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
	    return fromBank;
    }

}
