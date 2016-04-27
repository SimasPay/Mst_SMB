package com.mfino.flashiz.iso8583.processor.fixtoiso;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPaymentAuthorizationToBankForBsim;
import com.mfino.flashiz.iso8583.utils.DateTimeFormatter;
import com.mfino.flashiz.iso8583.utils.StringUtilities;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.DateTimeUtil;

public class PaymentAuthorizationToBankProcessor extends BankRequestProcessor{
	
	public PaymentAuthorizationToBankProcessor() {
	
		try{
			isoMsg.setMTI("0100");
		}
		catch (ISOException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public ISOMsg process(CFIXMsg fixmsg){
		CMPaymentAuthorizationToBankForBsim request = (CMPaymentAuthorizationToBankForBsim)fixmsg;
		Timestamp ts = new Timestamp();
		Timestamp localTS = DateTimeUtil.getLocalTime();
		int flag = 0;
		try
		{
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			String processingCode = "380000";
			isoMsg.set(3, processingCode);
			
			long tippingAmount = (request.getTippingAmount()!=null) ? request.getTippingAmount().longValue()*100: 0;
			long amount = (request.getAmount().longValue() - ((request.getTippingAmount()!=null) ? request.getTippingAmount().longValue(): 0)) * 100;
			
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", 18, "0"));
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts));
			log.info("Bsim PaymentAuthorizationToBankProcessor :: process timestamp in de-7 =" + DateTimeFormatter.getMMDDHHMMSS(ts));
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(12,DateTimeFormatter.getHHMMSS(localTS));
			isoMsg.set(13,DateTimeFormatter.getMMDD(localTS));
			isoMsg.set(18,CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone);
			isoMsg.set(22,constantFieldsMap.get("22"));
			isoMsg.set(24,constantFieldsMap.get("24"));
			isoMsg.set(25,constantFieldsMap.get("25"));
			isoMsg.set(32,constantFieldsMap.get("32"));
			//isoMsg.set(35,request.getSourceCardPAN());
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(request.getSourceMDN(), 15, " "));
			if(StringUtils.isNotBlank(request.getMerchantData())){
				isoMsg.set(43, StringUtilities.rightPadWithCharacter(request.getMerchantData(), 40, " "));
			}else{
				String defaultField43 = constantFieldsMap.get("43");
				isoMsg.set(43, StringUtilities.rightPadWithCharacter(defaultField43, 40, " "));
			}
			isoMsg.set(48,request.getUserAPIKey());
			isoMsg.set(49, constantFieldsMap.get("49"));
			isoMsg.set(54,StringUtilities.leftPadWithCharacter(tippingAmount + "", 18, "0"));
			isoMsg.set(61,request.getInvoiceNo());
			String fieldDe62 = createDe62(request);
			isoMsg.set(62,fieldDe62);
			isoMsg.set(98, StringUtilities.rightPadWithCharacter(constantFieldsMap.get("98"),25," "));
		}
		catch (ISOException ex) {
			log.error("Bsim PaymentAuthorizationToBankProcessor :: process ", ex);
		}
		return isoMsg;

	}

	private String createDe62(CMPaymentAuthorizationToBankForBsim request) {
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
}
