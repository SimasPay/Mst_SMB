package com.mfino.gt.iso8583.processor.fixtoiso;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.gt.iso8583.GetConstantCodes;
import com.mfino.gt.iso8583.utils.DateTimeFormatter;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;

public class MoneyTransferToGTProcessor extends GTBankRequestProcessor {
	// *FindbugsChange*
	// Previous -- public static String	MTI	= "0200";
	public static final String	MTI	= "0200";

	public MoneyTransferToGTProcessor() {
		try {
			isoMsg.setMTI(MTI);
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
			isoMsg.set(2, constantFieldsMap.get("2"));

			String processingCode = GetConstantCodes.getTransactionType(msg);
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getSourceBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getSourceBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("CHECKING_ACCOUNT");
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getDestinationBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getDestinationBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("CHECKING_ACCOUNT");

			isoMsg.set(3, processingCode);
			
			BigDecimal amount = msg.getAmount().multiply(new BigDecimal(100));
			isoMsg.set(4, amount.longValue() + ""); // 4
			
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); // 13
			isoMsg.set(14, DateTimeFormatter.getYYMM(ts));
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(18, constantFieldsMap.get("18")); // 18
			isoMsg.set(22, constantFieldsMap.get("22")); // 22
			isoMsg.set(23, constantFieldsMap.get("23")); // 23
			isoMsg.set(25, constantFieldsMap.get("25")); // 25
			isoMsg.set(26, constantFieldsMap.get("26")); // 26
			isoMsg.set(28, constantFieldsMap.get("28")); // 28
			isoMsg.set(30, constantFieldsMap.get("30")); // 30
			isoMsg.set(32, constantFieldsMap.get("32"));// 32
			isoMsg.set(33, constantFieldsMap.get("33"));// 33
			isoMsg.set(37, msg.getTransactionID().toString());// 37
			isoMsg.set(40, constantFieldsMap.get("40"));// 40
			isoMsg.set(41, generateField41(msg)); // 41
			isoMsg.set(42, constantFieldsMap.get("42"));// 42
			
			// Setting the Integration TxnId and comments in DE43 to identify the transaction in Basis easily. 
			String inTxnId = msg.getINTxnId();
			String comments = StringUtils.isNotBlank(msg.getComments()) ? msg.getComments() : StringUtils.EMPTY;
			
			if (StringUtils.isNotBlank(inTxnId)) {
				String de43 = (inTxnId + " " + comments + "                                       ").substring(0,40);
				isoMsg.set(43, de43); 
			}
			else {
			    isoMsg.set(43, constantFieldsMap.get("43")); // 43
			}
			
			isoMsg.set(49, constantFieldsMap.get("49")); // 49
			
			isoMsg.set(102, msg.getSourceCardPAN()); // 102
			isoMsg.set(103,msg.getDestCardPAN());
			
			isoMsg.set(123, constantFieldsMap.get("123"));
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
