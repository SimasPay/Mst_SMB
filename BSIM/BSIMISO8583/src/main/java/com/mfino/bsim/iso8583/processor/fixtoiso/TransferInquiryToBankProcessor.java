package com.mfino.bsim.iso8583.processor.fixtoiso;


import java.math.BigDecimal;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

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
		CMTransferInquiryToBank request = (CMTransferInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime();
		try {
			
			String mpan = MfinoUtil.CheckDigitCalculation(request.getSourceMDN());
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(2,mpan);
			String processingCode = "37";
			String sourceAccountType = "00";
			String destAcccountType = "00";
			
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("CHECKING_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Lakupandai.toString().equals(request.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("LAKUPANDAI_ACCOUNT");
			
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getDestinationBankAccountType()))
				destAcccountType = constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getDestinationBankAccountType()))
				destAcccountType = constantFieldsMap.get("CHECKING_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Lakupandai.toString().equals(request.getDestinationBankAccountType()))
				destAcccountType = constantFieldsMap.get("LAKUPANDAI_ACCOUNT");			
			
			isoMsg.set(3, processingCode + sourceAccountType + destAcccountType);
			//isoMsg.set(3,CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry);
			long amount = request.getAmount().longValue()*(100);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", 18, "0"));
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts));
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(12,DateTimeFormatter.getHHMMSS(localTS));
			isoMsg.set(13,DateTimeFormatter.getMMDD(localTS));
		    isoMsg.set(15,DateTimeFormatter.getMMDD(ts));
			isoMsg.set(18,CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone);
			isoMsg.set(22,constantFieldsMap.get("22"));
			isoMsg.set(25,constantFieldsMap.get("25"));
			isoMsg.set(26,constantFieldsMap.get("26"));
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString());
			isoMsg.set(32,constantFieldsMap.get("32"));
			isoMsg.set(33,constantFieldsMap.get("32"));
			//isoMsg.set(35,request.getSourceCardPAN());
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(request.getTransactionID().toString(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(request.getSourceMDN(), 15, " "));
			isoMsg.set(43, StringUtilities.rightPadWithCharacter("SMS MFINO", 40, " "));
			isoMsg.set(47, request.getTransactionID().toString());
			
			isoMsg.set(100, request.getBankCode().toString());
			isoMsg.set(102,request.getSourceCardPAN());
			isoMsg.set(103, request.getDestCardPAN());
			if(request.getLanguage().equals(0))
				   isoMsg.set(121,constantFieldsMap.get("english"));
				else
				   isoMsg.set(121,constantFieldsMap.get("Bahasa"));
			
			isoMsg.set(127, request.getBankCode().toString());
			
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
