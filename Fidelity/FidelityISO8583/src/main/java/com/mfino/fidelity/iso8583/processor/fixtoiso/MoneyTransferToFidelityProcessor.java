package com.mfino.fidelity.iso8583.processor.fixtoiso;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.utils.DateTimeFormatter;
import com.mfino.fidelity.iso8583.utils.FixToISOUtil;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;

public class MoneyTransferToFidelityProcessor extends FidelityISORequestProcessor {
	
	public MoneyTransferToFidelityProcessor() {
		try {
			isoMsg.setMTI("1200");
		}
		catch (ISOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		CMMoneyTransferToBank msg = (CMMoneyTransferToBank) fixmsg;
		Timestamp ts = msg.getTransferTime();

		// use the MDN of the global account
		String mdn = msg.getSourceMDNToUseForBank();
		if (mdn == null) {
			mdn = msg.getSourceMDN();
		}

		try {
			Long transactionID = msg.getTransactionID();
			
			isoMsg.set(2, constantFieldsMap.get("2"));
			isoMsg.set(3, "401010");			
			isoMsg.set(4, msg.getAmount().multiply(new BigDecimal(100)).longValue() + ""); // 4			
			isoMsg.set(11, FixToISOUtil.getPaddedSTAN(transactionID.toString()));			// STAN
			isoMsg.set(12, DateTimeFormatter.getYYYYMMDDhhmmss(ts)); // Local Transaction Date & Time
			isoMsg.set(15, DateTimeFormatter.getYYYYMMDD(ts));	 // Date, settlement
			isoMsg.set(17, DateTimeFormatter.getYYYYMMDD(ts));	 // Capture Date
			isoMsg.set(24, "200"); 							 // Functioncode
			isoMsg.set(32, constantFieldsMap.get("32"));	// Code identifying the acquirer
//			isoMsg.set(33, constantFieldsMap.get("33"));
			isoMsg.set(37, msg.getTransactionID().toString());// RRN
			isoMsg.set(41, constantFieldsMap.get("41")); 
			isoMsg.set(42, constantFieldsMap.get("42"));
			isoMsg.set(43, getTransactionDiscription(msg,true)); 
			isoMsg.set(49, constantFieldsMap.get("49")); 	  // Transaction currency Code
			isoMsg.set(102, msg.getSourceCardPAN()); 			// Account number to get details
			isoMsg.set(103, msg.getDestCardPAN());
			isoMsg.set(123, constantFieldsMap.get("123"));		//Delivery channel controller Id
//			isoMsg.set(124, constantFieldsMap.get("124"));
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}
	
	private String generateField41(CMMoneyTransferToBank msg) {
		String result = constantFieldsMap.get("41");
		if (StringUtils.isNotBlank(msg.getTerminalID())) {
			result = msg.getTerminalID();
		}
		return result;
	}
}
