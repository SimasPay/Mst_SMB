package com.mfino.iso8583.processor.bank.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public abstract class BankRequest {

	protected SinarmasISOMessage isoMsg ;

	protected WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		isoMsg.setBinary(false);
		
		CMBankRequest request = (CMBankRequest)fixmsg;
		
		isoMsg.setPAN(request.getSourceCardPAN());//2
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setTransmissionTime(ts);//7
		isoMsg.setSTAN(request.getTransactionID());//11
		isoMsg.setLocalTransactionTime(ts);//12
		isoMsg.setLocalTransactionDate(ts);//13
		isoMsg.setMerchantType(Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone));//18
		isoMsg.setAuthorizingIdentificationResponseLength(CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas);//27
		isoMsg.setAcquiringInstitutionIdentificationCode(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString());//32
		isoMsg.setForwardInstitutionIdentificationCode(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString());//33
		isoMsg.setTrack2Data(request.getSourceCardPAN());//35
		isoMsg.setRRN(request.getTransactionID().toString());//37
		isoMsg.setCardAcceptorIdentificationCode(request.getSourceMDN());//42
		isoMsg.setCardAcceptorNameLocation("SMS SMART");//43
		isoMsg.setPrivateTransactionID(request.getTransactionID().toString());//47
		
		return isoMsg;
	}
}
