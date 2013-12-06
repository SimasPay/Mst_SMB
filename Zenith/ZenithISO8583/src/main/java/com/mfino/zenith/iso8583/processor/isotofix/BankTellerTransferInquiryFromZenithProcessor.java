package com.mfino.zenith.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.mfino.zenith.iso8583.processor.IZenithBankISOtoFixProcessor;

public class BankTellerTransferInquiryFromZenithProcessor implements IZenithBankISOtoFixProcessor
{
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException
	{
		CMBankTellerTransferInquiryToBank toBank = (CMBankTellerTransferInquiryToBank)request;
		CMBankTellerTransferInquiryFromBank fromBank = new CMBankTellerTransferInquiryFromBank();
				
		fromBank.copy(toBank);
		fromBank.setIsInDirectCashIn(toBank.getIsInDirectCashIn());		fromBank.setServiceChargeTransactionLogID(toBank.getServiceChargeTransactionLogID());		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		
	    return fromBank;
	}
}
