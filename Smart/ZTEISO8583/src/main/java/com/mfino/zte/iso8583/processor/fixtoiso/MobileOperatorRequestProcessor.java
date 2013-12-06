package com.mfino.zte.iso8583.processor.fixtoiso;

import static com.mfino.zte.iso8583.utils.DateTimeFormatter.getHHMMSS;
import static com.mfino.zte.iso8583.utils.DateTimeFormatter.getMMDD;
import static com.mfino.zte.iso8583.utils.DateTimeFormatter.getMMDDHHMMSS;

import java.util.Map;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMOperatorRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.util.DateTimeUtil;

public abstract class MobileOperatorRequestProcessor implements IFixToIsoProcessor {

	protected ISOMsg	          isoMsg	= new ISOMsg();

	protected Map<String, String>	constantFieldsMap;
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	public int	TPM_UseBankNewCodes;

	public void setConstantFieldsMap(Map<String, String> map) {
		this.constantFieldsMap = map;
		TPM_UseBankNewCodes = Integer.parseInt(constantFieldsMap.get("TPM_UseBankNewCodes"));
	}

	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {

		CMOperatorRequest request = (CMOperatorRequest) fixmsg;

		try {
			isoMsg.set(2, constantFieldsMap.get("2"));// 2
			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.set(7, getMMDDHHMMSS(ts)); // 7
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, getHHMMSS(ts)); // 12
			isoMsg.set(13, getMMDD(ts)); // 13
			// isoMsg.setMerchantType(Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone));//18
			// isoMsg.setAuthorizingIdentificationResponseLength(CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas);//27
			isoMsg.set(32, CmFinoFIX.ISO8583_AcquiringInstIdCode_mFino_to_Bank.toString()); // 32
			isoMsg.set(33, CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_mFino_to_Bank.toString()); // 33
			// isoMsg.setTrack2Data(request.getSourceCardPAN()); //35
			isoMsg.set(37, request.getTransactionID().toString()); // 37
			isoMsg.set(42, request.getSourceMDN()); // 42 FIXME merchantcode?
			isoMsg.set(43, constantFieldsMap.get("43")); // 43
			                                        // FIXME
			                                        // merchant
			                                        // name
			                                        // location
//			isoMsg.set(123, request.getPOSDataCode()); // 123
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}

	
	

}
