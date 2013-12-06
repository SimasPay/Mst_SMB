package com.mfino.fidelity.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.GetConstantCodes;
import com.mfino.fidelity.iso8583.processor.FidelityISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class MoneyTransferReversalFromFidelityProcessor implements FidelityISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMMoneyTransferReversalFromBank response = new CMMoneyTransferReversalFromBank();
		CMMoneyTransferReversalToBank toBank = (CMMoneyTransferReversalToBank) request;

		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		response.copy(toBank);
		if(isoMsg.hasField(38))
			response.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39)){
			String respcode= isoMsg.getString(39);
			response.setResponseCode(GetConstantCodes.SUCCESS.equalsIgnoreCase(respcode)?CmFinoFIX.ISO8583_ResponseCode_Success:respcode);
		}
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());

		return response;
	}
	
}
