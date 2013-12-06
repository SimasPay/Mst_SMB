package com.mfino.bsim.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions1;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class NewSubscriberActivationToBankProcessor extends
		BankRequestProcessor {

	public NewSubscriberActivationToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		} catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg)
			throws AllElementsNotAvailableException {
		super.process(fixmsg);

		CMNewSubscriberActivationToBank msg = (CMNewSubscriberActivationToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime();

		try {
			isoMsg.set(2, msg.getInfo2());// msg.getSourceCardPAN()
			String processingCode = GetConstantCodes.getTransactionType(msg);

		    isoMsg.set(3,"900000");
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(
					11,
					StringUtilities.leftPadWithCharacter(
							transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			isoMsg.set(
					18,
					CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(24, "196");
			isoMsg.set(
					27,
					CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas
							.toString()); // 27
			isoMsg.set(32,
					CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas
							.toString());// 32
			isoMsg.set(
					33,
					CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas
							.toString());// 33
			//isoMsg.set(34, msg.getSourceMDN());
			isoMsg.set(37, StringUtilities.rightPadWithCharacter(msg
					.getTransactionID().toString(), 12, ""));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(
					msg.getSourceMDN(), 15, ""));
			isoMsg.set(
					43,
					StringUtilities.rightPadWithCharacter(
							constantFieldsMap.get("43"), 40, ""));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(102, msg.getSourceCardPAN());
		} catch (ISOException ex) {

		}
		return isoMsg;
	}
}
