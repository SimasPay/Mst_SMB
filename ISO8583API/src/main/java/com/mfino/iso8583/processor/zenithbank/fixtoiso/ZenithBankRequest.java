package com.mfino.iso8583.processor.zenithbank.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage; 
import com.mfino.util.DateTimeUtil;

public abstract class ZenithBankRequest {

	protected ZenithBankISOMessage isoMsg ;
	protected ZenithBankISOMessage element127Msg ;
	
	protected WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		isoMsg.setBinary(false);
		element127Msg.setBinary(false);
		
		CMBankRequest request = (CMBankRequest)fixmsg;
		
		isoMsg.setPAN(request.getSourceCardPAN());//2
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setTransmissionTime(ts);//7
		isoMsg.setSTAN(request.getTransactionID());//11
		isoMsg.setLocalTransactionTime(ts);//12
		isoMsg.setLocalTransactionDate(ts);//13
		//isoMsg.setMerchantType(Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone));//18
		//isoMsg.setAuthorizingIdentificationResponseLength(CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas);//27
		isoMsg.setAcquiringInstitutionIdentificationCode(CmFinoFIX.ISO8583_AcquiringInstIdCode_mFino_to_Bank.toString());	//32
		isoMsg.setForwardInstitutionIdentificationCode(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_mFino_to_Bank.toString());	//33
		//isoMsg.setTrack2Data(request.getSourceCardPAN());		//35
		isoMsg.setRRN(request.getTransactionID().toString());	//37
		isoMsg.setCardAcceptorIdentificationCode(request.getSourceMDN());					//42 FIXME merchantcode?
		isoMsg.setCardAcceptorNameLocation("ZMOBILE                             LANG");		//43 FIXME merchant name location
		isoMsg.setPOSDataCode(request.getPOSDataCode());									//123
		return isoMsg;
	}
	
	protected String buildProcessingCode(CMBankRequest request) {
		return null;
	}
	
}

