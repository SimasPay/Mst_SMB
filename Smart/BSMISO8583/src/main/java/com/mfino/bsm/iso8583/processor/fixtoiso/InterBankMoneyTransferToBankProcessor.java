package com.mfino.bsm.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.iso8583.utils.DateTimeFormatter;
import com.mfino.bsm.iso8583.utils.StringUtilities;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

/**
 * 
 * @author Amar
 *
 */
public class InterBankMoneyTransferToBankProcessor extends BankRequestProcessor {
	public static final String	MTI	= "0200";
	public InterBankMoneyTransferToBankProcessor() {
		try {
			isoMsg.setMTI(MTI);
		} catch (ISOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		CMInterBankMoneyTransferToBank msg = (CMInterBankMoneyTransferToBank) fixmsg;
		Timestamp ts = msg.getTransferTime();//changed to show transfer time as to show same thing in reversal. This is GMT Time
		Timestamp localTS = DateTimeUtil.getLocalTime();
		// use the MDN of the global account
		String mdn = msg.getSourceMDNToUseForBank();

		if (mdn == null) {
			mdn = msg.getSourceMDN();
		}

		try {
			isoMsg.set(2, msg.getMPan());			
			String processingCode = null;
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getSourceBankAccountType())){
				processingCode = "49" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
				if(msg.getProcessingCode()!=null){
					processingCode = "49" + constantFieldsMap.get("SAVINGS_ACCOUNT")+msg.getProcessingCode();
				}
			}
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getSourceBankAccountType())){
				processingCode = "49" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";
				if(msg.getProcessingCode()!=null){
					processingCode = "49" + constantFieldsMap.get("CHECKING_ACCOUNT")+msg.getProcessingCode();
				}
			}
			isoMsg.set(3, processingCode);
			Long amount = msg.getAmount().longValue()*(100);
			isoMsg.set(4, amount.longValue() + "");
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(22, constantFieldsMap.get("22")); // 18
			isoMsg.set(25, constantFieldsMap.get("25")); // 18
			isoMsg.set(26, constantFieldsMap.get("26")); // 18

			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));// 32 source bank code (bsim ibt)
			isoMsg.set(33, constantFieldsMap.get("32"));// 33 source bank code (bsim ibt)
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(msg.getTransactionID().toString(), 12, "0"));

			isoMsg.set(41, constantFieldsMap.get("41"));

			isoMsg.set(42, StringUtilities.rightPadWithCharacter(msg.getSourceMDN(), 15, " "));
			//isoMsg.set(43, "SMS MFINO");
			isoMsg.set(43, StringUtilities.rightPadWithCharacter("SMS MFINO", 40, " "));
			isoMsg.set(47, msg.getTransactionID().toString());

			isoMsg.set(48, msg.getAdditionalInfo());
			isoMsg.set(49, constantFieldsMap.get("49"));
			isoMsg.set(63, "00");
			isoMsg.set(100, msg.getDestBankCode().toString());
			isoMsg.set(102,msg.getSourceCardPAN());
			isoMsg.set(103,msg.getDestCardPAN());
			isoMsg.set(127, msg.getDestBankCode());
			if(msg.getLanguage().equals(0))
				isoMsg.set(121,constantFieldsMap.get("english"));
			else
				isoMsg.set(121,constantFieldsMap.get("bahasa"));
		}
		catch (ISOException ex) {
			log.error("MoneyTransferToBankProcessor process ", ex);
		}catch (Exception e) {
			log.error("MoneyTransferToBankProcessor process ", e);
		}
		return isoMsg;
	}
}
