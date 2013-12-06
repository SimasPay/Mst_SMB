package com.mfino.iso8583.processor.zenithbank.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;

public class MoneyTransferReversalFromZenith implements IZenithBankISOtoFixProcessor {

	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {

		CmFinoFIX.CMBankResponse response = new CmFinoFIX.CMBankResponse();
		CMMoneyTransferReversalToBank toBank = (CMMoneyTransferReversalToBank) request;

		if(!CmFinoFIX.ResponseCode_Success.toString().equals(isoMsg.getResponseCode()))
			return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		response.copy(toBank);
		response.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		response.setResponseCode(isoMsg.getResponseCode());

		return response;
	}
	
}
