package com.mfino.fidelity.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.utils.DateTimeFormatter;
import com.mfino.fidelity.iso8583.utils.FixToISOUtil;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class GetLastTrxnsToFidelityProcessor extends FidelityISORequestProcessor {

	public GetLastTrxnsToFidelityProcessor() {
		try {
			isoMsg.setMTI("1200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		
		CMGetLastTransactionsToBank msg = (CMGetLastTransactionsToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getLocalTime();

		try {
	
			isoMsg.set(2, constantFieldsMap.get("2"));			//Primary Account number/customer id
			isoMsg.set(3, "381000");	
			isoMsg.set(4, constantFieldsMap.get("ZERO_AMOUNT")); //Amount
			Long transactionID = msg.getTransactionID();
			isoMsg.set(11, FixToISOUtil.getPaddedSTAN(transactionID.toString()));			// STAN
			isoMsg.set(12, DateTimeFormatter.getYYYYMMDDhhmmss(ts)); // Local Transaction Date & Time
			isoMsg.set(15, DateTimeFormatter.getYYYYMMDD(ts));	 // Date, settlement
			isoMsg.set(17, DateTimeFormatter.getYYYYMMDD(ts));	 // Capture Date
			isoMsg.set(24, "200"); 							 // Functioncode
			isoMsg.set(32, constantFieldsMap.get("32"));	// Code identifying the acquirer
			isoMsg.set(37, msg.getTransactionID().toString());// RRN
//			isoMsg.set(41, constantFieldsMap.get("41")); 
//			isoMsg.set(42, constantFieldsMap.get("42"));
//			isoMsg.set(43, constantFieldsMap.get("43")); 
			isoMsg.set(49, constantFieldsMap.get("49")); 	  // Transaction currency Code
//			isoMsg.set(59, constantFieldsMap.get("59")); 	  // Transaction currency Code
//			isoMsg.set(93, constantFieldsMap.get("93")); 
//			isoMsg.set(94, constantFieldsMap.get("94")); 
			isoMsg.set(102, msg.getSourceCardPAN()); 			// Account number to get details
			isoMsg.set(123, constantFieldsMap.get("123"));		//Delivery channel controller Id
//			isoMsg.set(124, constantFieldsMap.get("124"));

		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}
}
