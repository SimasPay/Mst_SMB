package com.mfino.bsim.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class MoneyTransferToBankProcessor extends BankRequestProcessor {
	// *FindbugsChange*
	// Previous -- public static String	MTI	= "0200";
	public static final String	MTI	= "0200";

	public MoneyTransferToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		CMMoneyTransferToBank request = (CMMoneyTransferToBank) fixmsg;
		Timestamp localTS = DateTimeUtil.getLocalTime();
		Timestamp ts = request.getTransferTime();
		// use the MDN of the global account
		String mdn = request.getSourceMDNToUseForBank();
		if (mdn == null) {
			mdn = request.getSourceMDN();
		}

		try {
			String mpan = MfinoUtil.CheckDigitCalculation(request.getSourceMDN());
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(2,mpan);
			String processingCode = null;
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType())){
				processingCode = "49" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
			if(request.getProcessingCode()!=null){
				processingCode = "49" + constantFieldsMap.get("SAVINGS_ACCOUNT")+request.getProcessingCode();
			}
			}
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType())){
				processingCode = "49" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";
			if(request.getProcessingCode()!=null){
					processingCode = "49" + constantFieldsMap.get("CHECKING_ACCOUNT")+request.getProcessingCode();
			}
			}
			isoMsg.set(3, processingCode);
			long amount = request.getAmount().longValue()*(100);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", 18, "0"));
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts));
	        isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(12,DateTimeFormatter.getHHMMSS(localTS));
			isoMsg.set(13,DateTimeFormatter.getMMDD(localTS));
		    isoMsg.set(15,DateTimeFormatter.getMMDD(ts));
			isoMsg.set(18,CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone);
			isoMsg.set(22,constantFieldsMap.get(22));
			isoMsg.set(25,constantFieldsMap.get(25));
			isoMsg.set(26,constantFieldsMap.get(26));
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString());
			isoMsg.set(32,constantFieldsMap.get("32"));
			isoMsg.set(33,constantFieldsMap.get("32"));
			//isoMsg.set(35,request.getSourceCardPAN());
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(request.getTransactionID().toString(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(request.getSourceMDN(), 15, " "));
			isoMsg.set(43, StringUtilities.rightPadWithCharacter("SMS MFINO", 40, " "));
			isoMsg.set(47, request.getTransactionID().toString());
			isoMsg.set(48, request.getAdditionalInfo());
			isoMsg.set(100, request.getBankCode().toString());
			isoMsg.set(102,request.getSourceCardPAN());
			isoMsg.set(103, request.getDestCardPAN());
			if(request.getLanguage().equals(0))
				   isoMsg.set(121,constantFieldsMap.get("english"));
				else
				   isoMsg.set(121,constantFieldsMap.get("bahasa"));
			
			isoMsg.set(127, request.getBankCode().toString());
		    
		}
		catch (ISOException ex) {
			log.error("MoneyTransferToBankProcessor process ", ex);
		}catch (Exception e) {
			log.error("MoneyTransferToBankProcessor process ", e);
		}
		return isoMsg;
	}

	private String getProcessingCode(CMMoneyTransferToBank msg) {
		String processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut;
/*		if (TPM_UseBankNewCodes != 0)
			processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other1;
		else
			 processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;

		if (msg.getUICategory().equals(TransactionUICategory_EMoney_CashIn) || 
				msg.getUICategory().equals(TransactionUICategory_Dompet_EMoney_Trf)) {
			if (TPM_UseBankNewCodes != 0)
				 processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_CashIn1;
			else
				 processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_CashIn;
		}
		else if (msg.getUICategory().equals(TransactionUICategory_EMoney_Purchase) || 
				msg.getUICategory().equals(TransactionUICategory_EMoney_CashOut)
		        || msg.getUICategory().equals(TransactionUICategory_EMoney_Dompet_Trf)) {
			if (TPM_UseBankNewCodes != 0)
				 processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut1;
			else
				 processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut;
		}
		else {
			if (TPM_UseBankNewCodes != 0)
				 processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other1;
			else
				 processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;
		}*/
		return processingCode;
	}
}
