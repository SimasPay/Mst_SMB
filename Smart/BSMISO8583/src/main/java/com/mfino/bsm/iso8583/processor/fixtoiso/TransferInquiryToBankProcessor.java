package com.mfino.bsm.iso8583.processor.fixtoiso;


import java.math.BigDecimal;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.iso8583.utils.DateTimeFormatter;
import com.mfino.bsm.iso8583.utils.StringUtilities;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;

public class TransferInquiryToBankProcessor extends BankRequestProcessor {
	
	public TransferInquiryToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
//	     super.process(fixmsg);
		CMTransferInquiryToBank msg = (CMTransferInquiryToBank) fixmsg;
		Timestamp ts = new Timestamp();
		try {
			isoMsg.set(2, msg.getSourceCardPAN());
			String processingCode = getProcessingCode(msg);
			
			isoMsg.set(3, processingCode);

			BigDecimal amount = msg.getAmount();
			isoMsg.set(4, amount.longValue() + ""); // 4
			
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(22, constantFieldsMap.get("22")); // 18
			isoMsg.set(25, constantFieldsMap.get("25")); // 18
			isoMsg.set(26, constantFieldsMap.get("26")); // 18
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));// 32
			isoMsg.set(33, CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString());// 33
			isoMsg.set(35, msg.getSourceCardPAN());
			isoMsg.set(37, StringUtilities.rightPadWithCharacter(msg.getTransactionID().toString(), 12, " "));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(msg.getSourceMDN(), 15, " "));
			isoMsg.set(43, StringUtilities.rightPadWithCharacter("SMS SMART", 40, " "));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(48, msg.getTransactionID().toString());
			isoMsg.set(49, constantFieldsMap.get("49"));
			isoMsg.set(52, CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null));  
			
			isoMsg.set(100, msg.getBankCode().toString());
			isoMsg.set(102,msg.getSourceCardPAN());
			isoMsg.set(103,msg.getDestCardPAN());
			isoMsg.set(127, msg.getBankCode().toString()); //For now we have only one Bank BSIM, need to set dest bank code later
		}
		catch (ISOException ex) {
			log.error("TransferInquiryToBankProcessor :: process ", ex);
		}
		return isoMsg;
	}
	
	public String getProcessingCode(CMTransferInquiryToBank msg){
		String processingCode = CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut_Inquiry;
		/*
		 * if source if omnibus, then use Sinarmas_Transfer_CashIn_Inquiry 
		 */
/*		if (TPM_UseBankNewCodes != 0)
			processingCode =  ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry1;
		else
			processingCode =  ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry;

		if (msg.getUICategory().equals(TransactionUICategory_EMoney_CashIn)
				|| msg.getUICategory().equals(TransactionUICategory_Dompet_EMoney_Trf)) {
			if (TPM_UseBankNewCodes != 0)
				processingCode =  ISO8583_ProcessingCode_Sinarmas_Transfer_CashIn_Inquiry1;
			else
				processingCode =  ISO8583_ProcessingCode_Sinarmas_Transfer_CashIn_Inquiry;
		}
		else if (msg.getUICategory().equals(TransactionUICategory_EMoney_Purchase) 
				|| msg.getUICategory().equals(TransactionUICategory_EMoney_CashOut)
		        || msg.getUICategory().equals(TransactionUICategory_EMoney_Dompet_Trf)) {
			if (TPM_UseBankNewCodes != 0)
				processingCode =  ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut_Inquiry1;
			else
				processingCode =  ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut_Inquiry;
		}
		else {
			if (TPM_UseBankNewCodes != 0)
				processingCode =  ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry1;
			else
				processingCode =  ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry;
		}*/
		return processingCode;

	}
}
