package com.mfino.bsim.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.FixToISOUtil;
import com.mfino.bsim.iso8583.utils.StringUtilities;
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
			isoMsg.setMTI("0420");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}


	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {

		CMMoneyTransferReversalToBank msg = (CMMoneyTransferReversalToBank)fixmsg;

		// use the MDN of the global account
		String mdn = msg.getSourceMDNToUseForBank();
		if (mdn == null) {
			mdn = msg.getSourceMDN();
		}

		try {
			String mpan = MfinoUtil.CheckDigitCalculation(msg.getSourceMDN());
			isoMsg.set(2, mpan);
			String defaultDE3=ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;
			if(StringUtils.isNotBlank(msg.getProcessingCodeDE3())) {
				defaultDE3 = msg.getProcessingCodeDE3();
			}
			isoMsg.set(3,defaultDE3);

			long amount = msg.getAmount().longValue()*(100);
			isoMsg.set(4, StringUtilities.leftPadWithCharacter(amount+ "", 18, "0")); 

			Long stan = Long.parseLong(msg.getBankSystemTraceAuditNumber());
			stan = stan % 1000000;
			String paddedSTAN = FixToISOUtil.padOnLeft(stan.toString(), '0', 6);

			// create a new timestamp for the reversal as we cannot use the one
			// from moneytransferMessage
			Timestamp ts = DateTimeUtil.getGMTTime();
			Timestamp localTS = DateTimeUtil.getLocalTime();
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts));
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(22,constantFieldsMap.get("22"));
			isoMsg.set(25,constantFieldsMap.get("25"));
			isoMsg.set(26,constantFieldsMap.get("26"));
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, constantFieldsMap.get("32"));// 33
			//isoMsg.set(34, msg.getSourceCardPAN());//trac data
			isoMsg.set(37,StringUtilities.leftPadWithCharacter( msg.getTransactionID().toString(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, msg.getSourceMDN());
			isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(48, msg.getTransactionID().toString());
			String reversalInfoStr = "0200" + paddedSTAN + DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime());;

			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(constantFieldsMap.get("32"), '0', 11);
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(constantFieldsMap.get("32"), '0', 11);
			isoMsg.set(90, reversalInfoStr);

			isoMsg.set(100, msg.getBankCode().toString());
			isoMsg.set(102, msg.getSourceCardPAN()); 
			isoMsg.set(103, msg.getDestCardPAN());
//			if(msg.getLanguage().equals(0))
//				isoMsg.set(121,constantFieldsMap.get("english"));
//			else
//				isoMsg.set(121,constantFieldsMap.get("bahasa"));

			isoMsg.set(127, msg.getDestBankCode());//Destination Institution Code.

		}
		catch (ISOException ex) {
			log.error("MoneyTransferReversalToBankProcessor :: process ", ex);
		}catch (Exception e) {
			log.error("MoneyTransferReversalToBankProcessor :: process ", e);
		}
		return isoMsg;
	}
}
