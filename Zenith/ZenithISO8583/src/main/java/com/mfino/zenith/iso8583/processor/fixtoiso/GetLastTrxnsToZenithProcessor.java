package com.mfino.zenith.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.zenith.iso8583.GetConstantCodes;
import com.mfino.zenith.iso8583.utils.DateTimeFormatter;

public class GetLastTrxnsToZenithProcessor extends ZenithBankRequestProcessor {

	public GetLastTrxnsToZenithProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		super.process(fixmsg);

		CMGetLastTransactionsToBank msg = (CMGetLastTransactionsToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getLocalTime();

		try {
			isoMsg.set(2, msg.getSourceMDN());// msg.getSourceCardPAN()

			String processingCode = GetConstantCodes.getTransactionType(msg);
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getSourceBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getSourceBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("CHECKING_ACCOUNT");
			processingCode = processingCode + constantFieldsMap.get("UNSPECIFIED_ACCOUNT");

			isoMsg.set(3, processingCode); // 3
			isoMsg.set(4, constantFieldsMap.get("ZERO_AMOUNT")); // 4
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); // 13
			isoMsg.set(14, GetConstantCodes.getDE14(ts));// 14
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(22, constantFieldsMap.get("22")); // 22
			isoMsg.set(23, constantFieldsMap.get("23")); // 23
			isoMsg.set(25, constantFieldsMap.get("25")); // 25
			isoMsg.set(26, constantFieldsMap.get("26")); // 26
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(37, msg.getTransactionID().toString());
			isoMsg.set(41, constantFieldsMap.get("41")); // 41
			isoMsg.set(42, constantFieldsMap.get("42"));
			isoMsg.set(43, constructDE43(msg)); // 43
			isoMsg.set(49, constantFieldsMap.get("49")); // 49
			isoMsg.set(56, constantFieldsMap.get("56")); // 49
			isoMsg.set(59, msg.getTransactionID().toString()); // 59
			isoMsg.set(102, msg.getSourceCardPAN()); // 102
			isoMsg.set(123, constantFieldsMap.get("123"));
			isoMsg.set("127.0", GetConstantCodes.ZENITH_DE127_0);
			isoMsg.set("127.3", GetConstantCodes.ZENITH_DE127_3);
			isoMsg.set("127.20", DateTimeFormatter.getCCYYMMDD(ts));
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}
}
