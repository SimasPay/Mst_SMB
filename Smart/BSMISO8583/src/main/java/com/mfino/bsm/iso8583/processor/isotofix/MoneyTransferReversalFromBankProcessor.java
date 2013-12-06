package com.mfino.bsm.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.bsm.iso8583.GetConstantCodes;
import com.mfino.bsm.iso8583.ISOtoFIXProcessor;
import com.mfino.bsm.iso8583.processor.BSMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class MoneyTransferReversalFromBankProcessor implements BSMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMMoneyTransferReversalFromBank response = new CMMoneyTransferReversalFromBank();
		CMMoneyTransferReversalToBank toBank = (CMMoneyTransferReversalToBank) request;

		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		response.copy(toBank);
		if(isoMsg.hasField(38))
			response.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());

		return response;
	}
	
}
