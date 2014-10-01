package com.mfino.bsim.iso8583.processor.isotofix;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.iso.jpos.NoISOResponseException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class QRPaymentFromBankProcessor implements BSIMISOtoFixProcessor{

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMQRPaymentToBank toBank = (CMQRPaymentToBank)request;
		CMQRPaymentFromBank fromBank = new CMQRPaymentFromBank();

		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
        
		fromBank.copy(toBank);
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		if(isoMsg.hasField(48))
			fromBank.setBankAccountName(isoMsg.getString(48));
		if(isoMsg.hasField(62))
		{
			fromBank.setInfo1(isoMsg.getString(62));
	}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}
}