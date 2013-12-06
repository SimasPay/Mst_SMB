package com.mfino.fidelity.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.GetConstantCodes;
import com.mfino.fidelity.iso8583.processor.FidelityISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class AccountInquiryFromFidelityProcessor implements FidelityISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CmFinoFIX.CMBalanceInquiryFromBank response = new CmFinoFIX.CMBalanceInquiryFromBank();
		CMBalanceInquiryToBank toBank = (CMBalanceInquiryToBank) request;
	
		response.copy(toBank);
		
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
		
		//if the response is failure there is any point trying to parse the result
		if(GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		{
			response.setResponseCode(CmFinoFIX.ISO8583_ResponseCode_Success);
			String accountinfo = isoMsg.getString(125);
			String custID = accountinfo.substring(0, 9);
			String custname = accountinfo.substring(9, 89);
			String accountname = accountinfo.substring(89, 169);
		}
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
