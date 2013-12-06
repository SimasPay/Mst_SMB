package com.mfino.nfc.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas;
import java.math.BigDecimal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import com.mfino.nfc.iso8583.utils.DateTimeFormatter;
import com.mfino.nfc.iso8583.utils.FixToISOUtil;
import com.mfino.nfc.iso8583.utils.StringUtilities;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkReversalToCMS;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class NFCCardLinkReversalToCMSProcessor extends NFCRequestProcessor {

	public Log log = LogFactory.getLog(this.getClass());
	
	public NFCCardLinkReversalToCMSProcessor() {
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

		CMNFCCardLinkReversalToCMS msg = (CMNFCCardLinkReversalToCMS)fixmsg;

		
		try {
		   	//String mpan = MfinoUtil.CheckDigitCalculation(msg.getSourceMDN());
		   	String mpan = msg.getSourceCardPAN();
			isoMsg.set(2, mpan);
		    
			
			String processingCode = "500000";
			isoMsg.set(3, processingCode);
			
			Long stan = msg.getTransID();
			stan = stan % 1000000;
			String paddedSTAN = FixToISOUtil.padOnLeft(stan.toString(), '0', 6);

			// create a new timestamp for the reversal as we cannot use the one
			// from moneytransferMessage
			Timestamp ts = DateTimeUtil.getGMTTime();
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7

			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); // 13
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, constantFieldsMap.get("32"));// 33
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(msg.getTransID().toString(), 12,"0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42,StringUtilities.leftPadWithCharacter(msg.getSourceMDN(),15,""));
			isoMsg.set(43,StringUtilities.leftPadWithCharacter( constantFieldsMap.get("43"),40,""));
			isoMsg.set(48, msg.getSourceMDN());
			isoMsg.set(60,"NoISOResponse");
			String reversalInfoStr = "0200" + paddedSTAN;
			reversalInfoStr = reversalInfoStr+ DateTimeFormatter.getMMDDHHMMSS(ts);
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), '0', 11);
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), '0', 11);
			isoMsg.set(90, reversalInfoStr);
		}
		catch (ISOException ex) {
			log.error("NFCCardLinkReversalToBank :: process ", ex);
		}catch (Exception e) {
			log.error("NFCCardLinkReversalToBank :: process ", e);
		}
		return isoMsg;
	}
}
