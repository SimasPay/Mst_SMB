package com.mfino.zenith.iso8583.processor.fixtoiso;

import java.math.BigDecimal;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.zenith.iso8583.GetConstantCodes;
import com.mfino.zenith.iso8583.utils.DateTimeFormatter;

public class MoneyTransferReversalToZenithProcessor extends ZenithBankRequestProcessor {

	public MoneyTransferReversalToZenithProcessor() {
		try {
			isoMsg.setMTI("0420");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	public static String padOnLeft(String str, char paddingChar, int finalLength) throws Exception {
		if (finalLength == str.length())
			return str;
		if (finalLength < str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for (int i = 0; i < finalLength - str.length(); i++)
			s = s + String.valueOf(paddingChar);
		str = s + str;
		return str;
	}

	public static String padOnRight(String str, char paddingChar, int finalLength) throws Exception {
		if (finalLength == str.length())
			return str;
		if (finalLength < str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for (int i = 0; i < finalLength - str.length(); i++)
			s = s + String.valueOf(paddingChar);
		str = str + s;
		return str;
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);

		CMMoneyTransferReversalToBank msg = (CMMoneyTransferReversalToBank)fixmsg;

		// use the MDN of the global account
		String mdn = msg.getSourceMDNToUseForBank();
		if (mdn == null) {
			mdn = msg.getSourceMDN();
		}

		try {
			isoMsg.set(2, mdn);

			String processingCode = GetConstantCodes.getTransactionType(msg);
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getSourceBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getSourceBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("CHECKING_ACCOUNT");
			processingCode = processingCode + constantFieldsMap.get("UNSPECIFIED_ACCOUNT");

			isoMsg.set(3, processingCode); // 3

			// TODO:
			BigDecimal amount = msg.getAmount().multiply(new BigDecimal(100));
			isoMsg.set(4, amount.longValue() + ""); // 4

			// create a new timestamp for the reversal as we cannot use the one
			// from moneytransferMessage
			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7

			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); // 13

			isoMsg.set(14, GetConstantCodes.getDE14(ts)); // 14
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(16, DateTimeFormatter.getMMDD(ts)); // 16 FIXME
			
			isoMsg.set(22, constantFieldsMap.get("22")); // 22
			isoMsg.set(23, constantFieldsMap.get("23")); // 23
			isoMsg.set(25, constantFieldsMap.get("25")); // 25
			isoMsg.set(28, constantFieldsMap.get("28")); // 28
			isoMsg.set(30, constantFieldsMap.get("30")); // 28
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, constantFieldsMap.get("33"));
			
			isoMsg.set(37, msg.getTransactionID().toString());
			if (CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf.equals(msg.getUICategory())) {
				isoMsg.set(41, GetConstantCodes.ZENITH_DE41_BANK_SVA_TRF); // 41
				isoMsg.set(42, GetConstantCodes.ZENITH_DE42_BANK_SVA_TRF); // 42
			}
			else {
				// Currently only two scenarions Bank->Emoney or Emoney->Bank
				isoMsg.set(41, GetConstantCodes.ZENITH_DE41_SVA_BANK_TRF); // 41
				isoMsg.set(42, GetConstantCodes.ZENITH_DE42_SVA_BANK_TRF); // 42
			}

			isoMsg.set(43, constructDE43(msg)); // 43
			isoMsg.set(49, constantFieldsMap.get("49")); // 49

			isoMsg.set(54, "2053566D000000050000"); // 54 FIXME set additional
													// amounts based transaction
													// fees/costs
			isoMsg.set(56, "4021");
			// if(!StringUtils.isBlank(msg.getMessageReasonCode()))
			// isoMsg.set(56,msg.getMessageReasonCode()); //56
			isoMsg.set(59, transactionID.toString()); // 59
			String originalMsg;
			String AcquiringIdCode = isoMsg.getString("32");
			try {
	            AcquiringIdCode = padOnLeft(AcquiringIdCode, '0', 11);
	            String ForwardingIdCode = isoMsg.getString("33");
	            ForwardingIdCode = padOnLeft(ForwardingIdCode, '0', 11);
	            originalMsg = MoneyTransferToZenithProcessor.MTI
	            		+ // orginal message MTI
	            		padOnLeft(msg.getBankSystemTraceAuditNumber().toString(), '0', 6) + DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime())
	            		+ AcquiringIdCode + ForwardingIdCode;
	            isoMsg.set(90, originalMsg); // 90
            }
            catch (Exception ex) {
	            // TODO Auto-generated catch block
	            ex.printStackTrace();
            }
			isoMsg.set(95, "000000000000000000000000C00000000C00000000"); // 95
																		  // FIXME
																		  // set
																		  // amount,transaction
																		  // amounts,
																		  // settlement
																		  // ,transaction
																		  // fees
			// isoMsg.set(102,msg.getSourceCardPAN()); //102
			isoMsg.set(123, constantFieldsMap.get("123"));
			isoMsg.set("127.0", GetConstantCodes.ZENITH_DE127_0);
			// isoMsg.set("127.3", GetConstantCodes.ZENITH_DE127_3);
			// isoMsg.set("127.6","11");
			isoMsg.set("127.20", DateTimeFormatter.getCCYYMMDD(ts));
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}
}
