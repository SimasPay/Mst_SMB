package com.mfino.flashiz.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMPaymentAuthorizationFromBankForBsim;
import com.mfino.fix.CmFinoFIX.CMPaymentAuthorizationToBankForBsim;
import com.mfino.flashiz.iso8583.processor.FlashizISOtoFixProcessor;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class PaymentAuthorizationFromBankProcessor implements FlashizISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		CMPaymentAuthorizationToBankForBsim toBank = (CMPaymentAuthorizationToBankForBsim) request;
		CMPaymentAuthorizationFromBankForBsim fromBank = new CMPaymentAuthorizationFromBankForBsim();

		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));

		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}

}
