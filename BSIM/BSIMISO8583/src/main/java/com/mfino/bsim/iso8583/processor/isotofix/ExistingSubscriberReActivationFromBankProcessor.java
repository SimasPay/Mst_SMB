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
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class ExistingSubscriberReActivationFromBankProcessor implements BSIMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CmFinoFIX.CMExistingSubscriberReactivationFromBank response = new CmFinoFIX.CMExistingSubscriberReactivationFromBank();
		CMExistingSubscriberReactivationToBank toBank = (CMExistingSubscriberReactivationToBank) request;
        response.copy(toBank);
		if(isoMsg.hasField(38))
			response.setAIR(isoMsg.getString(38));

		if(isoMsg.hasField(39)){
			response.setResponseCode(isoMsg.getString(39));
	
			if(GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
			{
				if((isoMsg.hasField(102) && ((isoMsg.getString(102) != null) && !("".equalsIgnoreCase(isoMsg.getString(102)))))){
					response.setAccountNumber(isoMsg.getString(102));
				}
				else{
					response.setResponseCode("99");
				}
			}
		}

		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
