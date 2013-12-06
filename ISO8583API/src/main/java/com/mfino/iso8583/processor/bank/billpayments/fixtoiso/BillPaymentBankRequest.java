package com.mfino.iso8583.processor.bank.billpayments.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPaymentBankRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;
import com.mfino.util.DateTimeUtil;

public abstract class BillPaymentBankRequest {

	UMGH2HISOMessage	isoMsg;

	protected WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		isoMsg.setBinary(false);

		CMBillPaymentBankRequest request = (CMBillPaymentBankRequest) fixmsg;

		isoMsg.setPAN(request.getSourceCardPAN());//2
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setTransactionAmount(request.getAmount().toString() + "00");//4
		isoMsg.setTransmissionTime(ts);//7
		isoMsg.setSTAN(request.getTransactionID());//11
		isoMsg.setLocalTransactionTime(ts);//12
		isoMsg.setLocalTransactionDate(ts);//13
		isoMsg.setSettlementDate(ts);
		isoMsg.setMerchantType(Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone));//18
		isoMsg.setAuthorizingIdentificationResponseLength(CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas);//27
		isoMsg.setAcquiringInstitutionIdentificationCode(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString());//32
		isoMsg.setForwardInstitutionIdentificationCode(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString());//33
		isoMsg.setTrack2Data(request.getSourceCardPAN());//35
		isoMsg.setRRN(request.getTransactionID().toString());//37
		isoMsg.setCardAcceptorTerminalIdentification("11");//41
		isoMsg.setCardAcceptorIdentificationCode(request.getSourceMDN().toString());// 42
		isoMsg.setCardAcceptorNameLocation("SMS SMART");//43
		isoMsg.setTransactionCurrencyCode(CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_IDR);//49

		return isoMsg;
	}

}
