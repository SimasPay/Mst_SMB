package com.mfino.zenith.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

/**
 * @author Sasi
 *
 */
public class VisafoneAirtimePurchaseFromZenithProcessor extends MoneyTransferFromZenithProcessor
{
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException
	{
		CMVisafoneAirtimeMoneyTransferToBank toBank = (CMVisafoneAirtimeMoneyTransferToBank)request;
		CMVisafoneAirtimeMoneyTransferFromBank fromBank = new CMVisafoneAirtimeMoneyTransferFromBank();
				
		fromBank = (CMVisafoneAirtimeMoneyTransferFromBank)super.process(isoMsg, request);
		
	    return fromBank;
	}
}