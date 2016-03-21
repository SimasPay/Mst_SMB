package com.mfino.flashiz.iso8583.processor.fixtoiso;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionChargeLogDAO;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBankForBsim;
import com.mfino.flashiz.iso8583.utils.DateTimeFormatter;
import com.mfino.flashiz.iso8583.utils.StringUtilities;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.DateTimeUtil;

public class PaymentAcknowledgementToBankProcessor extends BankRequestProcessor{

	public PaymentAcknowledgementToBankProcessor() {

		try{
			isoMsg.setMTI("0220");
		}
		catch (ISOException e) {
			e.printStackTrace();
		}
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public ISOMsg process(CFIXMsg fixmsg){
		CMPaymentAcknowledgementToBankForBsim request = (CMPaymentAcknowledgementToBankForBsim)fixmsg;
		Timestamp ts = request.getTransferTime();//changed to show transfer time as to show same thing in reversal. This is GMT Time
		Timestamp localTS = DateTimeUtil.getLocalTime();
		int flag = 0;
		String fieldDE63 = constructDE63(request);
		try
		{
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			//dont send cardpan info and ac number in de-2 and de-102 to flashiz as it doesnot want flashiz to store user info.
			//isoMsg.set(2,request.getInfo2());
			String processingCode = "500000";
			if(request.getIsAdvice()!=null && request.getIsAdvice()==true){
				processingCode="510000";
			}
			isoMsg.set(3, processingCode);
			long amount = request.getAmount().longValue()*(100);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", 18, "0"));
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts));
			log.info("PaymentAcknowledgementToBankProcessor :: process timestamp in de-7 =" + DateTimeFormatter.getMMDDHHMMSS(ts));
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
			isoMsg.set(33,constantFieldsMap.get("33"));
			//isoMsg.set(35,request.getSourceCardPAN());
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(request.getTransactionID().toString(), 12, "0"));
			//if there is bank response then send same response to flashiz or else sent 68 in de-39
			if(StringUtils.isNotBlank(request.getResponseCodeString())){
				isoMsg.set(39,request.getResponseCodeString());
			}
			else{
				isoMsg.set(39,"68");
			}
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(request.getSourceMDN(), 15, " "));
			if(StringUtils.isNotBlank(request.getMerchantData())){
				isoMsg.set(43, StringUtilities.rightPadWithCharacter(request.getMerchantData(), 40, " "));
			}else{
				isoMsg.set(43, StringUtilities.rightPadWithCharacter("SMS MFINO", 40, " "));
			}
			isoMsg.set(48,request.getUserAPIKey());
			isoMsg.set(49, constantFieldsMap.get("49"));
			long tippingAmount = (request.getTippingAmount()!=null) ? request.getTippingAmount().longValue()*100: 0;
			isoMsg.set(54,StringUtilities.leftPadWithCharacter(tippingAmount + "", 18, "0"));
			
			if(request.getInfo3()!=null){
				isoMsg.set(61,request.getInfo3());
			}else{
				isoMsg.set(61,request.getInvoiceNo());
			}
			isoMsg.set(62,createDe62(request));
			//isoMsg.set(63,constructDE63(request));			
			//isoMsg.set(98,request.getBillerCode());
			isoMsg.set(98,StringUtilities.rightPadWithCharacter(constantFieldsMap.get("98"),25," "));
			//isoMsg.set(102,request.getSourceCardPAN());
		}
		catch (ISOException ex) {
			log.error("PaymentAcknowledgementToBankProcessor :: process ", ex);
		}
		return isoMsg;

	}

	private String createDe62(CMPaymentAcknowledgementToBankForBsim request) {
		String discountAmt = request.getDiscountAmount() != null?request.getDiscountAmount().toBigInteger().toString() :BigDecimal.ZERO.toBigInteger().toString();
		String numberOfCoupons = StringUtils.isNotBlank(request.getNumberOfCoupons())?request.getNumberOfCoupons().toString() : "0".toString();
		String discountType = StringUtils.isNotBlank(request.getDiscountType())?request.getDiscountType() : " ";
		String loyaltyName = StringUtils.isNotBlank(request.getLoyalityName())?request.getLoyalityName() : " ";
		String amountRedeemed = request.getAmountRedeemed() != null?request.getAmountRedeemed().toBigInteger().toString() :BigDecimal.ZERO.toBigInteger().toString();
		String pointsRedeemed = request.getPointsRedeemed() != null ? request.getPointsRedeemed().toString(): "0";

		String merchantApiKey = StringUtilities.leftPadWithCharacter(" ", 40, " ");
		discountAmt = StringUtilities.leftPadWithCharacter(discountAmt, 12, "0");
		numberOfCoupons = StringUtilities.leftPadWithCharacter(numberOfCoupons, 3, "0");
		discountType = StringUtilities.rightPadWithCharacter(discountType, 20, " ");
		loyaltyName = StringUtilities.rightPadWithCharacter(loyaltyName, 40, " ");
		amountRedeemed = StringUtilities.leftPadWithCharacter(amountRedeemed, 12, "0");
		pointsRedeemed = StringUtilities.leftPadWithCharacter(pointsRedeemed, 12, "0");

		String finalDe62 = merchantApiKey + discountAmt + numberOfCoupons + discountType + loyaltyName + pointsRedeemed + amountRedeemed;
		return finalDe62;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private String constructDE63(CMPaymentAcknowledgementToBankForBsim request) {
		Long sctlID = request.getServiceChargeTransactionLogID();
		TransactionChargeLogDAO tclDAO = DAOFactory.getInstance().getTransactionChargeLogDAO();

		BigDecimal serviceCharge = new BigDecimal(0);
		BigDecimal tax = new BigDecimal(0);
		String de63 = constantFieldsMap.get("63");
		String strServiceCharge, strTax;

		List <TransactionChargeLog> tclList = tclDAO.getBySCTLID(sctlID);
		if(CollectionUtils.isNotEmpty(tclList)){
			for(Iterator<TransactionChargeLog> it = tclList.iterator();it.hasNext();){
				TransactionChargeLog tcl = it.next();
				if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("charge")){
					serviceCharge = tcl.getCalculatedCharge();
				}
				if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("tax")){
					tax = tcl.getCalculatedCharge();
				}				
			}
		}

		strServiceCharge = "C" + StringUtilities.leftPadWithCharacter(serviceCharge.toBigInteger().toString(),8,"0");
		strTax = "C" + StringUtilities.leftPadWithCharacter(tax.toBigInteger().toString(),8,"0");
		de63 = StringUtilities.replaceNthBlock(de63, 'C', 12,strServiceCharge,9);
		de63 = StringUtilities.replaceNthBlock(de63, 'C', 13,strTax,9);
		return de63;
	}

}
