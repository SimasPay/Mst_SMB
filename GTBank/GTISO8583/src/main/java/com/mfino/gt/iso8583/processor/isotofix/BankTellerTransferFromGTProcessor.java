package com.mfino.gt.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.gt.iso8583.processor.IGTBankISOtoFixProcessor;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class BankTellerTransferFromGTProcessor implements IGTBankISOtoFixProcessor
{
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException 
	{
		CMBankTellerMoneyTransferToBank toBank = (CMBankTellerMoneyTransferToBank)request;
		CMBankTellerMoneyTransferFromBank fromBank = new CMBankTellerMoneyTransferFromBank();
				
		fromBank.copy(toBank);
		fromBank.setServiceChargeTransactionLogID(toBank.getServiceChargeTransactionLogID());		fromBank.setIsInDirectCashIn(toBank.getIsInDirectCashIn());		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		
	    return fromBank;
	}
}
