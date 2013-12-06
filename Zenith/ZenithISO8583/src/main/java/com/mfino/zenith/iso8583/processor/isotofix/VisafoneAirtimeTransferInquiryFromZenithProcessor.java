package com.mfino.zenith.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

/**
 * @author Sasi
 *
 */
public class VisafoneAirtimeTransferInquiryFromZenithProcessor extends TransferInquiryFromZenithProcessor {

	public VisafoneAirtimeTransferInquiryFromZenithProcessor()
	{
		super();
	}
	
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMVisafoneAirtimeTransferInquiryToBank toBank = (CMVisafoneAirtimeTransferInquiryToBank)request;
		CMVisafoneAirtimeTransferInquiryFromBank fromBank = new CMVisafoneAirtimeTransferInquiryFromBank();
		
		fromBank = (CMVisafoneAirtimeTransferInquiryFromBank)super.process(isoMsg, request);
		
	    return fromBank;
    }
}
