package com.mfino.bsm.ppob.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.ppob.iso8583.utils.DateTimeFormatter;
import com.mfino.bsm.ppob.iso8583.utils.StringUtilities;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.DateTimeUtil;

public class BillPaymentToBankProcessor extends BankRequestProcessor{
	
	public BillPaymentToBankProcessor() {
	
		try{
			isoMsg.setMTI("0200");
		}
		catch (ISOException e) {
			e.printStackTrace();
		}
	}
	
	public ISOMsg process(CFIXMsg fixmsg){
		CMBSIMBillPaymentToBank request = (CMBSIMBillPaymentToBank)fixmsg;
		Timestamp ts = request.getTransferTime();//changed to show transfer time as to show same thing in reversal. This is GMT Time
		Timestamp localTS = DateTimeUtil.getLocalTime();
		try
		{
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
//			isoMsg.set(2,request.getSourceCardPAN()); [Bala] As per the new Spec this field is not required. 25/09/14
			String processingCode = "501000";
			if(request.getBillerPartnerType() != null && request.getSourceBankAccountType() !=null){

				if(request.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Denomination) || request.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Free)){
					if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType()))
						processingCode = "56" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
					else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType()))
						processingCode = "56" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";
				}
				else{
					if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType()))
						processingCode = "50" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
					else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType()))
						processingCode = "50" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";
				}
			}
			
			isoMsg.set(3, processingCode);
			long amount = request.getAmount().longValue()*(100);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", constantFieldsMap.get("4").length(), "0"));
			
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts));
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(12,DateTimeFormatter.getHHMMSS(localTS));
			isoMsg.set(13,DateTimeFormatter.getMMDD(localTS));
			isoMsg.set(15,DateTimeFormatter.getMMDD(ts));
			isoMsg.set(18,constantFieldsMap.get("DE18"));
			isoMsg.set(22,constantFieldsMap.get("22"));
			isoMsg.set(25,constantFieldsMap.get("25"));
			isoMsg.set(26,constantFieldsMap.get("26"));
			isoMsg.set(32,constantFieldsMap.get("32"));
			isoMsg.set(33,constantFieldsMap.get("33"));
//			isoMsg.set(35,request.getSourceCardPAN()); [Bala] As per the new Spec this field is not required. 25/09/14
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(request.getTransactionID().toString(), 12, "0"));
			
			isoMsg.set(40,constantFieldsMap.get("40"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(request.getSourceMDN(), 15, " "));
			isoMsg.set(43,constantFieldsMap.get("43"));			
			isoMsg.set(49, constantFieldsMap.get("49"));
			if(request.getAdditionalInfo() != null)
				isoMsg.set(61, request.getAdditionalInfo());
			else
				isoMsg.set(61,request.getInvoiceNo().replaceAll("[+]", ""));
			isoMsg.set(62, request.getAdditionalInfo());
			isoMsg.set(98, StringUtilities.rightPadWithCharacter(request.getBillerCode(),25," "));
			isoMsg.set(102,request.getSourceCardPAN());
			if ( (null != request.getLanguage()) && !(request.getLanguage().equals(0)) ) {
				isoMsg.set(121,constantFieldsMap.get("bahasa"));
			}
			else {
				isoMsg.set(121,constantFieldsMap.get("english"));
			}			
		}
		catch (ISOException ex) {
			log.error("BillPaymentsToBankProcessor :: process ", ex);
		}
		return isoMsg;
		
	}
}
