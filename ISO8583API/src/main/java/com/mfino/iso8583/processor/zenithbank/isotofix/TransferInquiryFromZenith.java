package com.mfino.iso8583.processor.zenithbank.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor; 
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage; 
import com.mfino.util.DateTimeUtil;

public class TransferInquiryFromZenith implements IZenithBankISOtoFixProcessor{
	
	@Override
    public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {
		
		CMTransferInquiryToBank toBank = (CMTransferInquiryToBank)request;
		CMTransferInquiryFromBank fromBank = new CMTransferInquiryFromBank();
		
		if(!CmFinoFIX.ISO8583_ResponseCode_Success.equals(isoMsg.getResponseCode()))
			return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
		
		fromBank.copy(toBank);
		fromBank.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		fromBank.setResponseCode(isoMsg.getResponseCode());
		//fromBank.setBankAccountName(isoMsg.getSianrmasBankAccountName());
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		
	    return fromBank;
    }

}
