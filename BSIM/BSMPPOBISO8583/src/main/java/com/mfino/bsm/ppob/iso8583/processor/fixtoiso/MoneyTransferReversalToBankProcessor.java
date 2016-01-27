package com.mfino.bsm.ppob.iso8583.processor.fixtoiso;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.ppob.iso8583.utils.DateTimeFormatter;
import com.mfino.bsm.ppob.iso8583.utils.FixToISOUtil;
import com.mfino.bsm.ppob.iso8583.utils.StringUtilities;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

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
			isoMsg.set(2, MfinoUtil.CheckDigitCalculation(msg.getSourceMDN()));
			
			String processingCode = null;
			String sourceAccountType = "00";
			String destAccountType = "00";
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("CHECKING_ACCOUNT");			
			
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getDestinationBankAccountType()))
				destAccountType = constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getDestinationBankAccountType()))
				destAccountType = constantFieldsMap.get("CHECKING_ACCOUNT");
			
			processingCode = "49" + sourceAccountType + destAccountType;
			isoMsg.set(3, processingCode);
			
			long amount = msg.getAmount().longValue()*(100);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", constantFieldsMap.get("4").length(), "0"));
			
			
			Long stan = Long.parseLong(msg.getBankSystemTraceAuditNumber());
			stan = stan % 1000000;
			String paddedSTAN = FixToISOUtil.padOnLeft(stan.toString(), '0', 6);

			Timestamp ts = DateTimeUtil.getGMTTime();
			Timestamp localTS = DateTimeUtil.getLocalTime();
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7

			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts));
			isoMsg.set(18, constantFieldsMap.get("18")); // 18
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, constantFieldsMap.get("33"));// 33
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(msg.getTransactionID().toString(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(msg.getSourceMDN(), 15, " "));
			isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(48, msg.getTransactionID().toString());
			isoMsg.set(49, constantFieldsMap.get("49"));
			String reversalInfoStr = "0200" + paddedSTAN;
			reversalInfoStr = reversalInfoStr+ DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime());
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(constantFieldsMap.get("32"), '0', 11);
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(constantFieldsMap.get("33"), '0', 11);
			isoMsg.set(90, reversalInfoStr);
			isoMsg.set(100, msg.getBankCode().toString());
			isoMsg.set(102, msg.getSourceCardPAN()); 
			isoMsg.set(103, msg.getDestCardPAN());
			String field127 = StringUtils.isNotBlank(msg.getDestBankCode()) ? 
					msg.getDestBankCode() : msg.getBankCode().toString();
			isoMsg.set(127, field127);//Destination Institution Code.
			
		}
		catch (ISOException ex) {
			log.error("MoneyTransferReversalToBankProcessor :: process ", ex);
		}catch (Exception e) {
			log.error("MoneyTransferReversalToBankProcessor :: process ", e);
		}
		return isoMsg;
	}
}
