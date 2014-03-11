package com.mfino.flashiz.iso8583.processor.isotofix;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.flashiz.iso8583.processor.FlashizISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class PaymentAcknowledgementFromBankProcessor implements FlashizISOtoFixProcessor{

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMPaymentAcknowledgementToBank toBank = (CMPaymentAcknowledgementToBank)request;
		CMPaymentAcknowledgementFromBank fromBank = new CMPaymentAcknowledgementFromBank();

		if(isoMsg.hasField(39))
			fromBank.setResponseCode(Integer.parseInt(isoMsg.getString(39)));

		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}

}
