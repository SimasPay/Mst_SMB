package com.mfino.nfc.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.nfc.iso8583.utils.DateTimeFormatter;
import com.mfino.nfc.iso8583.utils.StringUtilities;
import com.mfino.util.DateTimeUtil;

public class BalanceInquiryToBankProcessor extends NFCRequestProcessor {

	public BalanceInquiryToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);
		CMBalanceInquiryToBank request = (CMBalanceInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
        try 
        {    
        	if(request.getSourceCardPAN() != null)
        	{
        		isoMsg.set(2, request.getSourceCardPAN()); 
        	}
			String processingCode = CmFinoFIX.ISO8583_ProcessingCode_NFC_Balance_Inquiry;
			isoMsg.set(3,processingCode);
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts));
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(),6 ,"0"));
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts));
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts));
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, constantFieldsMap.get("33"));
			isoMsg.set(37, StringUtilities.rightPadWithCharacter(request.getTransactionID().toString(), 12,"0"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(request.getSourceMDN(),15,""));
			isoMsg.set(43, StringUtilities.rightPadWithCharacter( constantFieldsMap.get("43"),40," "));
			isoMsg.set(48, request.getSourceMDN()); 	
       }
		catch (ISOException e) {
			log.error("Exception in creating an ISOMessage. " + e.getMessage());
		}	
		return isoMsg;
	}

}