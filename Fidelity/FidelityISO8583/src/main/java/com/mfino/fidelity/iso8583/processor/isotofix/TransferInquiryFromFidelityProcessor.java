package com.mfino.fidelity.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.GetConstantCodes;
import com.mfino.fidelity.iso8583.processor.FidelityISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class TransferInquiryFromFidelityProcessor implements FidelityISOtoFixProcessor{
	
	@Override
    public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMTransferInquiryToBank toBank = (CMTransferInquiryToBank)request;
		CMTransferInquiryFromBank fromBank = new CMTransferInquiryFromBank();
		
		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
		
		fromBank.copy(toBank);
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39)){
			String respcode = isoMsg.getString(39);
			fromBank.setResponseCode(GetConstantCodes.SUCCESS.equalsIgnoreCase(respcode)?CmFinoFIX.ISO8583_ResponseCode_Success:respcode);
		}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
	    return fromBank;
    }

}
