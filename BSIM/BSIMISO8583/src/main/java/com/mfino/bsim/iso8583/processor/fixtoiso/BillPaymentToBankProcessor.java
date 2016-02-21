package com.mfino.bsim.iso8583.processor.fixtoiso;

import java.math.BigDecimal;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
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

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public ISOMsg process(CFIXMsg fixmsg){
		CMBSIMBillPaymentToBank request = (CMBSIMBillPaymentToBank)fixmsg;
		Timestamp ts = request.getTransferTime();//changed to show transfer time as to show same thing in reversal. This is GMT Time
		Timestamp localTS = DateTimeUtil.getLocalTime();
		int flag = 0;
		try
		{
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(2,request.getInfo2());
			String processingCode = "56";
			String sourceAccountType = "00";
			String destAcccountType = "00";
			
			if(request.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Denomination) || 
					request.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Free)) {
				processingCode = "56";
				flag = 1;
			}
			else {
				processingCode = "50";
			}
			
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("CHECKING_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Lakupandai.toString().equals(request.getSourceBankAccountType()))
				sourceAccountType = constantFieldsMap.get("LAKUPANDAI_ACCOUNT");
			
			if(request.getProcessingCodeDE3()!=null){
				destAcccountType = request.getProcessingCodeDE3();
			}			

			isoMsg.set(3, processingCode + sourceAccountType + destAcccountType);			
			long amount = request.getAmount().longValue()*(100);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", 18, "0"));
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts));
			log.info("BillPaymentToBankProcessor :: process timestamp in de-7 =" + DateTimeFormatter.getMMDDHHMMSS(ts));
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
			isoMsg.set(49, constantFieldsMap.get("49"));
			if(request.getInfo3()!=null){
				isoMsg.set(61,request.getInfo3());
			}else{
				isoMsg.set(61,request.getInvoiceNo());
			}
			isoMsg.set(63,constructDE63(request));			
			isoMsg.set(98,request.getBillerCode());
			isoMsg.set(102,request.getSourceCardPAN());
			if(request.getLanguage().equals(0))
				isoMsg.set(121,constantFieldsMap.get("english"));
			else
				isoMsg.set(121,constantFieldsMap.get("bahasa"));

		}
		catch (ISOException ex) {
			log.error("BillPaymentsToBankProcessor :: process ", ex);
		}
		return isoMsg;

	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private String constructDE63(CMBSIMBillPaymentToBank request) {
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

}
