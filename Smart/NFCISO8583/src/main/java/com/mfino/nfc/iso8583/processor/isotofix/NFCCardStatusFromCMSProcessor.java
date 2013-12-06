package com.mfino.nfc.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMNFCCardStatusToCMS;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.nfc.iso8583.processor.NFCISOtoFixProcessor;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class NFCCardStatusFromCMSProcessor implements NFCISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CmFinoFIX.CMNFCCardStatusFromCMS response = new CmFinoFIX.CMNFCCardStatusFromCMS();
		CMNFCCardStatusToCMS toBank = (CMNFCCardStatusToCMS) request;
		response.copy(toBank);
		if(isoMsg.hasField(61)){
			response.setCardStatusText(isoMsg.getString(61));
		}	
		if(isoMsg.hasField(39)){
			response.setResponseCodeString(isoMsg.getString(39));
		}
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
