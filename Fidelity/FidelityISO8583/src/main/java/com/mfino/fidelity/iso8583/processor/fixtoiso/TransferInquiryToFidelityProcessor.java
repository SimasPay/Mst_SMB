package com.mfino.fidelity.iso8583.processor.fixtoiso;

import java.math.BigDecimal;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.utils.DateTimeFormatter;
import com.mfino.fidelity.iso8583.utils.FixToISOUtil;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;

public class TransferInquiryToFidelityProcessor extends FidelityISORequestProcessor {

	public TransferInquiryToFidelityProcessor() {
		try {
			isoMsg.setMTI("1200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);
		CMTransferInquiryToBank msg = (CMTransferInquiryToBank) fixmsg;
		Timestamp ts = new Timestamp();
		try {
			Long transactionID = msg.getTransactionID();
			
			isoMsg.set(2, msg.getSourceCardPAN());
			isoMsg.set(3, "410000");			//change processing code if used for other transactions
			isoMsg.set(4, msg.getAmount().multiply(new BigDecimal(100)).longValue() + ""); // 4			
			isoMsg.set(11, FixToISOUtil.getPaddedSTAN(transactionID.toString()));			// STAN
			isoMsg.set(12, DateTimeFormatter.getYYYYMMDDhhmmss(ts)); // Local Transaction Date & Time
			isoMsg.set(15, DateTimeFormatter.getYYYYMMDD(ts));	 // Date, settlement
			isoMsg.set(17, DateTimeFormatter.getMMDD(ts));	 // Capture Date
			isoMsg.set(24, "200"); 							 // Functioncode
			isoMsg.set(32, constantFieldsMap.get("32"));	// Code identifying the acquirer
			isoMsg.set(37, msg.getTransactionID().toString());// RRN
			isoMsg.set(43, getTransactionDiscription(msg, false));// for now inquiry is to simulator so no need for ReconcilationID
			isoMsg.set(49, constantFieldsMap.get("49")); 	  // Transaction currency Code
			isoMsg.set(102, msg.getSourceCardPAN()); 			// source account
			isoMsg.set(103, msg.getDestCardPAN());				//dest account
			isoMsg.set(123, constantFieldsMap.get("123"));		//Delivery channel controller Id
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}
	
	
}
