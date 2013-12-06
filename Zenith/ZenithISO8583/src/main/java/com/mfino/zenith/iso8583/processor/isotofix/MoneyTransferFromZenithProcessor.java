package com.mfino.zenith.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.mfino.zenith.iso8583.GetConstantCodes;
import com.mfino.zenith.iso8583.ISOtoFIXProcessor;
import com.mfino.zenith.iso8583.processor.IZenithBankISOtoFixProcessor;

public class MoneyTransferFromZenithProcessor implements IZenithBankISOtoFixProcessor{
	
	@Override
    public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMMoneyTransferToBank toBank = (CMMoneyTransferToBank)request;
		CMMoneyTransferFromBank fromBank = new CMMoneyTransferFromBank();
		
		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
		
		fromBank.copy(toBank);
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		
	    return fromBank;
    }
}
