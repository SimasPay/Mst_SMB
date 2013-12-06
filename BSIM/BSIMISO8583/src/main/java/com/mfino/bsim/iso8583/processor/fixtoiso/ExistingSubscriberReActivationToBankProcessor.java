package com.mfino.bsim.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions1;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.bsim.iso8583.utils.CryptoUtil;
import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.handlers.hsm.HSMHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class ExistingSubscriberReActivationToBankProcessor extends
		BankRequestProcessor {

	public ExistingSubscriberReActivationToBankProcessor() {
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
        CMExistingSubscriberReactivationToBank msg = (CMExistingSubscriberReactivationToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime();

		try {
			//isoMsg.set(2, msg.getInfo2());// msg.getSourceMDN()
			isoMsg.set(2,msg.getSourceCardPAN());
			String processingCode = null;
//			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getSourceBankAccountType()))
//				processingCode = "90" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
//			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getSourceBankAccountType()))
//				processingCode = "90" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";
			isoMsg.set(3, constantFieldsMap.get("PROCESSING_CODE_REACTIVATION"));
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
			isoMsg.set(22,constantFieldsMap.get("22"));
			isoMsg.set(25,constantFieldsMap.get("25"));
			isoMsg.set(26,constantFieldsMap.get("26"));
			isoMsg.set(24, "196");// For Re-activation - 196; For Activation - 196
			isoMsg.set(
					27,
					CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas
							.toString()); // 27
			isoMsg.set(32,constantFieldsMap.get("32"));
					;// 32
			isoMsg.set(33,constantFieldsMap.get("32"));// 33
			//isoMsg.set(34, msg.getSourceCardPAN());
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(msg
					.getTransactionID().toString(), 12, "0"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(
					msg.getSourceMDN(), 15, ""));
			isoMsg.set(
					43,
					StringUtilities.rightPadWithCharacter(
							constantFieldsMap.get("43"), 40, ""));
			isoMsg.set(47, msg.getTransactionID().toString());
			//isoMsg.set(52, CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null));
			HSMHandler handler  = new HSMHandler();
			String pinblock = handler.generatePinBlock(msg.getSourceCardPAN(), msg.getPin());
//			isoMsg.set(52,CryptoUtil.buildEncryptedPinBlock(msg.getPin(), msg.getSourceCardPAN()));
			isoMsg.set(52,pinblock);
			isoMsg.set(102, msg.getSourceCardPAN());
			if ((msg.getLanguage() != null ) && (msg.getLanguage().equals(0)))
				   isoMsg.set(121,constantFieldsMap.get("english"));
				else
				   isoMsg.set(121,constantFieldsMap.get("bahasa"));
			
		} catch (ISOException ex) {
			log.error("ISOException while constructing the ISO message for Re-Activation :" + ex.getMessage(), ex);
		}catch(Exception e){
			log.error("Error while constructing the ISO message for Re-Activation :" + e.getMessage(), e);
		}
		return isoMsg;
	}
}
