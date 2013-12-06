package com.mfino.fidelity.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.ISO93BPackager;

import com.mfino.fidelity.iso8583.utils.DateTimeFormatter;
import com.mfino.fidelity.iso8583.utils.FixToISOUtil;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class BalanceInquiryToFidelityProcessor extends FidelityISORequestProcessor {

	public BalanceInquiryToFidelityProcessor() {
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
		CMBalanceInquiryToBank msg = (CMBalanceInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getLocalTime();
		Long transactionID = msg.getTransactionID();
		

		try {
			isoMsg.set(2, constantFieldsMap.get("2"));
			isoMsg.set(3, "310000");
			isoMsg.set(4, constantFieldsMap.get("ZERO_AMOUNT")); // 4 "0000000000000000"
			isoMsg.set(11, FixToISOUtil.getPaddedSTAN(transactionID.toString()));// Anp12 but iso N6
			isoMsg.set(12, DateTimeFormatter.getYYYYMMDDhhmmss(ts)); // date time ISO YY
			isoMsg.set(17, DateTimeFormatter.getYYYYMMDD(ts));  // ISO YY
			isoMsg.set(24, "200"); 
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(37, msg.getTransactionID().toString());
//			isoMsg.set(41, constantFieldsMap.get("41")); 
//			isoMsg.set(42, constantFieldsMap.get("42"));
//			isoMsg.set(43, constantFieldsMap.get("43")); 
			isoMsg.set(49, constantFieldsMap.get("49")); // Transaction currency Code
			isoMsg.set(102, msg.getSourceCardPAN()); // 102
			isoMsg.set(123, constantFieldsMap.get("123"));
//			isoMsg.set(124, constantFieldsMap.get("124"));
			
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}

	public static void main(String[] args) throws Exception {

		CMBalanceInquiryToBank msg = new CMBalanceInquiryToBank();
		msg.setSourceCardPAN("55555555");
		msg.setSourceBankAccountType(CmFinoFIX.BankAccountType_Saving.toString());
		msg.setTransactionID(12345l);

		BalanceInquiryToFidelityProcessor toZ = new BalanceInquiryToFidelityProcessor();
		ISOMsg isoMsg = toZ.process(msg);

		ISOPackager packager = new ISO93BPackager();
		isoMsg.setPackager(packager);
		System.out.println(new String(isoMsg.pack()));

	}

}