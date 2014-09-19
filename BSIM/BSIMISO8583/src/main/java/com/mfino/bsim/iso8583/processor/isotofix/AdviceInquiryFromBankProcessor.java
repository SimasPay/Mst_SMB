package com.mfino.bsim.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMAdviceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMAdviceInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class AdviceInquiryFromBankProcessor implements BSIMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CMAdviceInquiryFromBank response = new CMAdviceInquiryFromBank();
		CMAdviceInquiryToBank toBank = (CMAdviceInquiryToBank) request;
		
		response.copy(toBank);
		if(isoMsg.hasField(3))
			response.setProcessingCodeDE3(isoMsg.getString(3).substring(4, 6));
		if(isoMsg.hasField(38))
			response.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
		response.setInfo1(isoMsg.getString(61));
		response.setInfo3(isoMsg.getString(62));
		if(isoMsg.hasField(63))
			response.setServiceChargeDE63(isoMsg.getString(63));
		
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
