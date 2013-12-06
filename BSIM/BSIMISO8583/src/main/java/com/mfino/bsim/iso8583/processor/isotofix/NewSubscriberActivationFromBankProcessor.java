package com.mfino.bsim.iso8583.processor.isotofix;

import java.math.BigDecimal;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.AdditionalAmounts;
import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class NewSubscriberActivationFromBankProcessor implements BSIMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CmFinoFIX.CMNewSubscriberActivationFromBank response = new CmFinoFIX.CMNewSubscriberActivationFromBank();
		CMNewSubscriberActivationToBank toBank = (CMNewSubscriberActivationToBank) request;
        response.copy(toBank);
		if(isoMsg.hasField(38))
			response.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
	
		//if the response is failure there is any point trying to parse the result
		if(GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		{
	    response.setAccountNumber(isoMsg.getString(102));
	    }
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
