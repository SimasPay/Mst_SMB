package com.mfino.bsm.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other1;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.iso8583.utils.CryptoUtil;
import com.mfino.bsm.iso8583.utils.DateTimeFormatter;
import com.mfino.bsm.iso8583.utils.FixToISOUtil;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class MoneyTransferReversalToBankProcessor extends BankRequestProcessor {

	public Log log = LogFactory.getLog(this.getClass());
	
	public MoneyTransferReversalToBankProcessor() {
		try {
			isoMsg.setMTI("0400");
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
			isoMsg.set(2, msg.getSourceCardPAN());
			
			String processingCode = sinarmasTransferReversalCode;// Can be changed in mce_bsm_iso_configuration
			isoMsg.set(3, processingCode);
			
			// TODO:
			BigDecimal amount = msg.getAmount();
			isoMsg.set(4, amount.longValue() + "");
			
			Long stan = Long.parseLong(msg.getBankSystemTraceAuditNumber());
			stan = stan % 1000000;
			String paddedSTAN = FixToISOUtil.padOnLeft(stan.toString(), '0', 6);

			// create a new timestamp for the reversal as we cannot use the one
			// from moneytransferMessage
			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime())); // 7

			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(msg.getTransferTime())); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(msg.getTransferTime())); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(msg.getTransferTime()));

			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(22,constantFieldsMap.get("22"));
			isoMsg.set(25,constantFieldsMap.get("25"));
			isoMsg.set(26,constantFieldsMap.get("26"));
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString());// 33
			isoMsg.set(35, msg.getSourceCardPAN());//trac data
			isoMsg.set(37, msg.getBankRetrievalReferenceNumber());
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, msg.getSourceMDN());
			isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(48, msg.getTransactionID().toString());
			isoMsg.set(52, CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null)); //
			String reversalInfoStr = "0200" + paddedSTAN;
			reversalInfoStr = reversalInfoStr+ DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime());
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), '0', 11);
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), '0', 11);
			isoMsg.set(90, reversalInfoStr);
			
			isoMsg.set(100, msg.getBankCode().toString());

			isoMsg.set(102, msg.getSourceCardPAN()); 
			isoMsg.set(103, msg.getDestCardPAN());
			isoMsg.set(127, String.valueOf(msg.getBankCode()));//Destination Institution Code.
			
		}
		catch (ISOException ex) {
			log.error("MoneyTransferReversalToBankProcessor :: process ", ex);
		}catch (Exception e) {
			log.error("MoneyTransferReversalToBankProcessor :: process ", e);
		}
		return isoMsg;
	}
}
