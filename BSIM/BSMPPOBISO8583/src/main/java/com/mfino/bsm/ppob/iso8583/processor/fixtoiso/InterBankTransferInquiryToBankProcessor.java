package com.mfino.bsm.ppob.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.ppob.iso8583.utils.DateTimeFormatter;
import com.mfino.bsm.ppob.iso8583.utils.StringUtilities;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

/**
 * 
 * @author Amar
 *
 */
public class InterBankTransferInquiryToBankProcessor extends BankRequestProcessor {
	public InterBankTransferInquiryToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		} catch (ISOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ISOMsg process(CFIXMsg fixmsg)
			throws AllElementsNotAvailableException {
		CMInterBankTransferInquiryToBank msg = (CMInterBankTransferInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime(); 
		try {
			isoMsg.set(2, msg.getMPan());
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
			
			processingCode = "37" + sourceAccountType + destAccountType;
			isoMsg.set(3, processingCode);
			
			long amount = msg.getAmount().longValue()*(100);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", constantFieldsMap.get("4").length(), "0"));
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(18, constantFieldsMap.get("DE18")); // 18
			isoMsg.set(22, constantFieldsMap.get("22")); // 18
			isoMsg.set(25, constantFieldsMap.get("25")); // 18
			isoMsg.set(26, constantFieldsMap.get("26")); // 18
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));// 32 source bank code (bsim ibt)
			isoMsg.set(33, constantFieldsMap.get("33"));// 33 source bank code (bsim ibt)
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(msg.getTransactionID().toString(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(msg.getSourceMDN(), 15, " "));
			isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(49, constantFieldsMap.get("49"));
			isoMsg.set(100, msg.getBankCode().toString());
			isoMsg.set(102, msg.getSourceCardPAN());
			isoMsg.set(103, msg.getDestCardPAN());
			isoMsg.set(127, msg.getDestBankCode());  //Destination bank code / inter bank code (bsim ibt)
		}
		catch (ISOException ex) {
			log.error("TransferInquiryToBankProcessor :: process ", ex);
		}
		return isoMsg;
	}
	
}
