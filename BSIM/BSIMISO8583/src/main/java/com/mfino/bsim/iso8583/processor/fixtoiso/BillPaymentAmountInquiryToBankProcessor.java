package com.mfino.bsim.iso8583.processor.fixtoiso;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentInquiryToBank;
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
		String fieldDE63 = constructDE63(request);
		try
		{
			isoMsg.set(2,request.getInfo2());
			String processingCode = null;
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType()))
			 processingCode = "38" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType()))
			 processingCode = "38" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";
			if(processingCode==null){
				processingCode = "381000";
			}
			isoMsg.set(3, processingCode);
			isoMsg.set(4, StringUtilities.leftPadWithCharacter(0 + "", 18, "0")); // 4
			if(request.getAmount()!=null && StringUtils.isNotBlank(request.getAmount().toString())){
				long amount = request.getAmount().longValue()*(100);
				isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", 18, "0"));
			}
			//isoMsg.set(3,CmFinoFIX.ISO8583_ProcessingCode_Artajasa_Bills_Inquiry0);
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts));
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(12,DateTimeFormatter.getHHMMSS(localTS));
			isoMsg.set(13,DateTimeFormatter.getMMDD(localTS));
			isoMsg.set(14,DateTimeFormatter.getMMDD(ts));
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
			//isoMsg.set(40,service)
			isoMsg.set(49, constantFieldsMap.get("49"));
			//For Paymentmode of packagetype send customerid and packagetype in de-62 Simobi BillPay Phase2
			if(CmFinoFIX.PaymentMode_PackageType.equalsIgnoreCase(request.getPaymentMode())){
				isoMsg.set(61, request.getAmount()+request.getInvoiceNo());
			}else{
				isoMsg.set(61, request.getInvoiceNo());
			}
			isoMsg.set(63,fieldDE63);
			isoMsg.set(98, request.getBillerCode());
			isoMsg.set(102,request.getSourceCardPAN());
			isoMsg.set(121,constantFieldsMap.get("english"));
			}
		catch (ISOException ex) {
			log.error("BillPaymentsToBankProcessor :: process ", ex);
		}
		return isoMsg;
		
	}
	
	private String constructDE63(CMBSIMBillPaymentInquiryToBank request) {
		BigDecimal serviceCharge = request.getServiceChargeAmount();
		BigDecimal tax = request.getTaxAmount();
		if(serviceCharge == null) {
			serviceCharge = new BigDecimal(0);
		}
		if(tax == null) {
			tax = new BigDecimal(0);
		}
		String de63 = constantFieldsMap.get("63");
		String strServiceCharge, strTax;
		
		strServiceCharge = "C" + StringUtilities.leftPadWithCharacter(serviceCharge.toBigInteger().toString(),8,"0");
		strTax = "C" + StringUtilities.leftPadWithCharacter(tax.toBigInteger().toString(),8,"0");
		de63 = StringUtilities.replaceNthBlock(de63, 'C', 12,strServiceCharge,9);
		de63 = StringUtilities.replaceNthBlock(de63, 'C', 13,strTax,9);
		return de63;
	}
	

	public Set<String> getOfflineBillers() {
		return offlineBillers;
	}

	public void setOfflineBillers(Set<String> offlineBillers) {
		this.offlineBillers = offlineBillers;
	}
}
