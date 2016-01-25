package com.mfino.bsm.ppob.iso8583.processor.fixtoiso;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.ppob.iso8583.utils.DateTimeFormatter;
import com.mfino.bsm.ppob.iso8583.utils.StringUtilities;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.DateTimeUtil;

public class BillPaymentAmountInquiryToBankProcessor extends BankRequestProcessor{
	
	private Set<String> offlineBillers;
	
	public BillPaymentAmountInquiryToBankProcessor() {
		try{
			isoMsg.setMTI("0200");
		}
		catch (ISOException e) {
			e.printStackTrace();
		}
	}
	
	public ISOMsg process(CFIXMsg fixmsg){
		CMBSIMGetAmountToBiller request = (CMBSIMGetAmountToBiller)fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime();
		Long transactionID = request.getTransactionID();
		transactionID = transactionID % 1000000;
		
		try
		{
//			isoMsg.set(2,request.getSourceCardPAN()); [Bala] As per the new Spec this field is not required. 25/09/14
			String processingCode = null;
			String sourceAccountType = "00";
			String destAccountType = "00";
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("CHECKING_ACCOUNT");			
			
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getDestinationBankAccountType()))
				destAccountType = constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getDestinationBankAccountType()))
				destAccountType = constantFieldsMap.get("CHECKING_ACCOUNT");
			
			processingCode = "38" + sourceAccountType + destAccountType;
			isoMsg.set(3, processingCode);
			isoMsg.set(4, StringUtilities.leftPadWithCharacter(0 + "", constantFieldsMap.get("4").length(), "0")); // 4
			if(request.getAmount()!=null && StringUtils.isNotBlank(request.getAmount().toString())){
				long amount = request.getAmount().longValue()*(100);
				isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", constantFieldsMap.get("4").length(), "0"));
			}

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
			isoMsg.set(40, constantFieldsMap.get("40"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(request.getSourceMDN(), 15, " "));
			isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(49, constantFieldsMap.get("49"));
			if(CmFinoFIX.PaymentMode_PackageType.equalsIgnoreCase(request.getPaymentMode()))
				isoMsg.set(61, request.getAmount()+request.getInvoiceNo().replaceAll("[+]", ""));
			else
				isoMsg.set(61, request.getInvoiceNo().replaceAll("[+]", ""));
		
			isoMsg.set(98, StringUtilities.rightPadWithCharacter(request.getBillerCode(), 25, " "));
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

	public Set<String> getOfflineBillers() {
		return offlineBillers;
	}

	public void setOfflineBillers(Set<String> offlineBillers) {
		this.offlineBillers = offlineBillers;
	}
}
