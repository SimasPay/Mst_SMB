package com.mfino.bsm.uangku.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.bsm.uangku.iso8583.processor.BSMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * 
 * @author Amar
 *
 */
public class InterBankMoneyTransferFromBankProcessor implements BSMISOtoFixProcessor{

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMInterBankMoneyTransferToBank toBank = (CMInterBankMoneyTransferToBank)request;
		CMInterBankMoneyTransferFromBank fromBank = new CMInterBankMoneyTransferFromBank();
		
		fromBank.copy(toBank);
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		if(isoMsg.hasField(37))
			fromBank.setBillPaymentReferenceID(isoMsg.getString(37));
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		
	    return fromBank;
	}

}
